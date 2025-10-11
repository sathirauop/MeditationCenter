package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 404 Not Found error response.
 * Used when a requested resource cannot be found.
 */
public record NotFoundErrorResponse(String message) implements ErrorResponse {
}
