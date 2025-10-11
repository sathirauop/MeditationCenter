package com.isipathana.meditationcenter.exception.response;

/**
 * Represents HTTP 409 Conflict error response.
 * Used when the request conflicts with the current state of the server.
 * Common cases: duplicate email, resource version conflict, etc.
 */
public record ConflictErrorResponse(String message) implements ErrorResponse {
}
