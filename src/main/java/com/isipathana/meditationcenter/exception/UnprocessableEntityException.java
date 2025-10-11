package com.isipathana.meditationcenter.exception;

import lombok.Getter;

/**
 * Represents an exception that occurs when a request is syntactically correct
 * but semantically invalid (business rule violation).
 * Returns an HTTP 422 Unprocessable Entity status code.
 *
 * Common use cases:
 * - Booking date is in the past
 * - Program is full (max seats reached)
 * - Payment amount doesn't match booking amount
 * - User trying to register for event they already registered for
 */
@Getter
public class UnprocessableEntityException extends RuntimeException {
    private final String errorCode;

    public UnprocessableEntityException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public UnprocessableEntityException(String message) {
        super(message);
        this.errorCode = null;
    }
}
