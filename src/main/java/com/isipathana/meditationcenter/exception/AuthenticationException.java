package com.isipathana.meditationcenter.exception;

/**
 * Represents an exception that occurs when authentication fails.
 * Returns an HTTP 401 Unauthorized status code.
 *
 * Common use cases:
 * - Invalid credentials (wrong password)
 * - Missing authentication token
 * - Expired token
 * - Invalid token
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Exception e) {
        super(message, e);
    }
}
