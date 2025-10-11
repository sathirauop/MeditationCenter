package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 422 Unprocessable Entity error response.
 * Used when the request is syntactically correct but semantically invalid (business rule violation).
 *
 * @param code    Machine-readable error code (e.g., "BOOKING_DATE_PAST", "PROGRAM_FULL")
 * @param message Human-readable error message
 */
public record UnprocessableEntityErrorResponse(String code, String message) implements ErrorResponse {
}
