package com.isipathana.meditationcenter.exception;

/**
 * Represents an exception that occurs when a user is authenticated but not authorized.
 * Returns an HTTP 403 Forbidden status code.
 *
 * Common use cases:
 * - User trying to access admin-only resources
 * - User trying to modify another user's data
 * - Insufficient permissions for the operation
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Exception e) {
        super(message, e);
    }
}
