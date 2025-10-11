package com.isipathana.meditationcenter.exception.response.factory;

import com.isipathana.meditationcenter.exception.*;
import com.isipathana.meditationcenter.exception.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Default implementation of ErrorResponseFactory.
 * Creates standard error responses for each exception type.
 * <p>
 * Security considerations:
 * - Authentication/Forbidden errors use generic messages to avoid information leakage
 * - Internal server errors hide sensitive details from clients
 * - Validation/Business errors expose specific details to help clients fix their requests
 */
@Component
public class DefaultErrorResponseFactory extends ErrorResponseFactory {

    @Override
    protected BadRequestErrorResponse createBadRequestErrorResponse(BadRequestException e) {
        return new BadRequestErrorResponse(e.getMessage());
    }

    @Override
    protected ValidationErrorResponse createValidationErrorResponse(ValidationException e) {
        return new ValidationErrorResponse(e.getField(), e.getMessage());
    }

    @Override
    protected AuthenticationErrorResponse createAuthenticationErrorResponse(
            AuthenticationException e) {
        // Use generic message for security - don't reveal if user exists or not
        return new AuthenticationErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    protected ForbiddenErrorResponse createForbiddenErrorResponse(ForbiddenException e) {
        // Use generic message for security - don't reveal resource structure
        return new ForbiddenErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase());
    }

    @Override
    protected NotFoundErrorResponse createNotFoundErrorResponse(ResourceNotFoundException e) {
        // Use generic message for security - don't reveal if resource exists
        return new NotFoundErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Override
    protected ConflictErrorResponse createConflictErrorResponse(ConflictException e) {
        return new ConflictErrorResponse(e.getMessage());
    }

    @Override
    protected UnprocessableEntityErrorResponse createUnprocessableEntityErrorResponse(
            UnprocessableEntityException e) {
        return new UnprocessableEntityErrorResponse(e.getErrorCode(), e.getMessage());
    }

    @Override
    protected InternalServerErrorResponse createInternalServerErrorResponse(Exception e) {
        // Never expose internal error details to clients
        return new InternalServerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
