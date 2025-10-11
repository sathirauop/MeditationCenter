package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 400 Bad Request error response.
 * Used for general bad requests that don't fit validation errors.
 */
public record BadRequestErrorResponse(String message) implements ErrorResponse {
}
