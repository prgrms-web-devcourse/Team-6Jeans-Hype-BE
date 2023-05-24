package com.example.demo.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

	long waitTime() default 100L;

	long leaseTime() default 3L;

	TimeUnit unit() default TimeUnit.SECONDS;
}
