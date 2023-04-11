package com.example.demo.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class TimeLoggingAspect {
	private static final Logger logger = LoggerFactory.getLogger(TimeLoggingAspect.class);

	@Around("@annotation(com.example.demo.common.aop.MeasureTime)")
	public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();

		Object result = joinPoint.proceed();

		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;

		logger.info("****** Scheduled method [{}] execution time : {}ms ******", joinPoint.getSignature(),
			executionTime);

		return result;
	}
}
