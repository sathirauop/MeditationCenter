package com.isipathana.meditationcenter.exception;

/**
 * Represents an exception that occurs when a conflict is detected.
 * Returns an HTTP 409 Conflict status code.
 *
 * Common use cases:
 * - Email already exists
 * - Duplicate booking for same date/time
 * - Resource version conflict
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Exception e) {
        super(message, e);
    }
}
