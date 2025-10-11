package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 403 Forbidden error response.
 * Used when the user is authenticated but doesn't have permission to access the resource.
 */
public record ForbiddenErrorResponse(String message) implements ErrorResponse {
}
