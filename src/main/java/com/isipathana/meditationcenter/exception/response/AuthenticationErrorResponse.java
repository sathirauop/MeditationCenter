package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 401 Unauthorized error response.
 * Used when authentication is required but has failed or not been provided.
 */
public record AuthenticationErrorResponse(String message) implements ErrorResponse {
}
