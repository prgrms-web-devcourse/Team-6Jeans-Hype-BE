package com.example.demo.common.aop;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {
	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.example.demo.common.aop.DistributedLock)")
	public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock annotation = method.getAnnotation(DistributedLock.class);

		String postId = Stream.of(joinPoint.getArgs())
			.filter(arg -> arg instanceof Long)
			.map(Object::toString)
			.findFirst()
			.orElse(null);
		String lockName = String.format("%s:%s", method.getName(), postId);
		RLock rLock = redissonClient.getLock(lockName);

		try {
			boolean available = rLock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.unit());
			if (!available) {
				return false;
			}

			return aopForTransaction.proceed(joinPoint);
		} catch (InterruptedException e) {
			log.error("Generate Interrupt!!");
			throw new InterruptedException();
		} finally {
			try {
				log.info("Redisson Lock Unlock Complete {} {}",
					method.getName(),
					lockName);
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
				log.info("Redisson Lock Already Unlock {} {}",
					method.getName(),
					lockName);
			}
		}
	}
}
