package com.example.demo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		AuthenticationException exception) throws IOException, ServletException {
		logger.error("Responding with unauthorized error. Message - {}", exception.getMessage());
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
			exception.getLocalizedMessage());
	}
}
