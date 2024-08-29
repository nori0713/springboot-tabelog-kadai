package com.example.nagoyameshi.exception;

import org.springframework.security.core.AuthenticationException;

public class SubscriptionExpiredException extends AuthenticationException {

	public SubscriptionExpiredException(String message) {
		super(message);
	}

	public SubscriptionExpiredException(String message, Throwable cause) {
		super(message, cause);
	}
}