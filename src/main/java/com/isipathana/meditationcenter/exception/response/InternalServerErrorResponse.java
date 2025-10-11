package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 500 Internal Server Error response.
 * Used for unexpected server errors.
 * Note: The message should not expose sensitive internal details to clients.
 */
public record InternalServerErrorResponse(String message) implements ErrorResponse {
}
