package com.isipathana.meditationcenter.exception.response.factory;

import com.isipathana.meditationcenter.exception.*;
import com.isipathana.meditationcenter.exception.response.*;

/**
 * Abstract factory for creating ErrorResponse objects from exceptions.
 * Subclasses define how to map each exception type to its corresponding response.
 * Uses the Factory pattern with Java 21 pattern matching in switch expressions.
 */
public abstract class ErrorResponseFactory {

    protected abstract BadRequestErrorResponse createBadRequestErrorResponse(BadRequestException e);

    protected abstract ValidationErrorResponse createValidationErrorResponse(ValidationException e);

    protected abstract AuthenticationErrorResponse createAuthenticationErrorResponse(
            AuthenticationException e);

    protected abstract ForbiddenErrorResponse createForbiddenErrorResponse(ForbiddenException e);

    protected abstract NotFoundErrorResponse createNotFoundErrorResponse(ResourceNotFoundException e);

    protected abstract ConflictErrorResponse createConflictErrorResponse(ConflictException e);

    protected abstract UnprocessableEntityErrorResponse createUnprocessableEntityErrorResponse(
            UnprocessableEntityException e);

    protected abstract InternalServerErrorResponse createInternalServerErrorResponse(Exception e);

    /**
     * Creates an appropriate ErrorResponse based on the exception type.
     * Uses Java 21 pattern matching in switch expressions.
     *
     * @param e The exception to convert
     * @return An ErrorResponse implementation corresponding to the exception type
     */
    public ErrorResponse createErrorResponse(Exception e) {
        return switch (e) {
            case BadRequestException badRequestException ->
                    createBadRequestErrorResponse(badRequestException);
            case ValidationException validationException ->
                    createValidationErrorResponse(validationException);
            case AuthenticationException authenticationException ->
                    createAuthenticationErrorResponse(authenticationException);
            case ForbiddenException forbiddenException ->
                    createForbiddenErrorResponse(forbiddenException);
            case ResourceNotFoundException resourceNotFoundException ->
                    createNotFoundErrorResponse(resourceNotFoundException);
            case ConflictException conflictException ->
                    createConflictErrorResponse(conflictException);
            case UnprocessableEntityException unprocessableEntityException ->
                    createUnprocessableEntityErrorResponse(unprocessableEntityException);
            case null, default -> createInternalServerErrorResponse(e);
        };
    }
}
