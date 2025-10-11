package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 400 Bad Request error response for validation errors.
 * Includes the field name that failed validation and the error message.
 *
 * @param field   The field that failed validation (may be null for general validation errors)
 * @param message The validation error message
 */
public record ValidationErrorResponse(String field, String message) implements ErrorResponse {
}
