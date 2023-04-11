package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.example.demo.common.aop.TimeLoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

	@Bean
	public TimeLoggingAspect timeLoggingAspect() {
		return new TimeLoggingAspect();
	}
}
