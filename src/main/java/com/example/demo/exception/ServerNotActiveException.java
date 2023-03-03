package com.example.demo.exception;

public class ServerNotActiveException extends RuntimeException {
	public ServerNotActiveException(String message) {
		super(message);
	}
}
