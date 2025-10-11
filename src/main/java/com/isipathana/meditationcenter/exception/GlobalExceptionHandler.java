package com.isipathana.meditationcenter.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.isipathana.meditationcenter.exception.response.ErrorResponse;
import com.isipathana.meditationcenter.exception.response.factory.ErrorResponseFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * Global exception handler for REST API endpoints.
 * Extends ResponseEntityExceptionHandler to handle Spring MVC exceptions.
 * Uses @RestControllerAdvice to apply to all @RestController classes.
 * <p>
 * Exception handling strategy:
 * - Validation errors (400) -> INFO logging (client error)
 * - Authentication/Authorization (401/403) -> WARN logging (security concern)
 * - Not Found (404) -> WARN logging
 * - Business errors (409/422) -> WARN logging (business rule violation)
 * - Internal errors (500) -> ERROR logging (server problem)
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger;
    private final ErrorResponseFactory errorResponseFactory;

    /**
     * Override to customize internal exception handling.
     * Converts exception to ErrorResponse using the factory.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        return body == null
                ? new ResponseEntity<>(errorResponseFactory.createErrorResponse(ex), headers, statusCode)
                : new ResponseEntity<>(body, headers, statusCode);
    }

    /**
     * Handles Spring @Valid validation failures.
     * Converts field errors to ValidationException and then to ValidationErrorResponse.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ErrorResponse> errorResponses =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error ->
                                new ValidationException(
                                        convertToSnakeCase(error.getField()),
                                        error.getDefaultMessage()))
                        .map(errorResponseFactory::createErrorResponse)
                        .toList();

        return handleExceptionInternal(
                ex,
                errorResponses.isEmpty() ? null : errorResponses,
                headers,
                status,
                request);
    }

    /**
     * Handles JSON parsing errors (missing request body, invalid JSON, type mismatches).
     * Converts MismatchedInputException to ValidationException with field information.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = "Required request body is missing.";

        ValidationException exception =
                switch (ex.getCause()) {
                    case MismatchedInputException mie -> ValidationException.fromException(mie);
                    case null -> new ValidationException(null, message);
                    default -> new ValidationException(null, ex.getCause().getMessage());
                };

        return handleExceptionInternal(exception, null, headers, status, request);
    }

    /**
     * Handles ValidationException - field-level validation errors.
     * HTTP 400 Bad Request.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(
            ValidationException ex, WebRequest request) {

        logger.info("Validation error: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles BadRequestException - general bad requests.
     * HTTP 400 Bad Request.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {

        logger.info("Bad request: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles AuthenticationException - authentication failures.
     * HTTP 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        logger.warn("Authentication failed: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles ForbiddenException - insufficient permissions.
     * HTTP 403 Forbidden.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(
            ForbiddenException ex, WebRequest request) {

        logger.warn("Forbidden access attempt: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handles ResourceNotFoundException - resource not found.
     * HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        logger.warn("Resource not found: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles ConflictException - resource conflicts (e.g., duplicate email).
     * HTTP 409 Conflict.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(
            ConflictException ex, WebRequest request) {

        logger.warn("Conflict detected: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Handles UnprocessableEntityException - business rule violations.
     * HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Object> handleUnprocessableEntityException(
            UnprocessableEntityException ex, WebRequest request) {

        if (!ex.getMessage().isBlank()) {
            logger.warn("Business rule violation: {} (code: {})", ex.getMessage(), ex.getErrorCode());
        }

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    /**
     * Catches all unhandled exceptions.
     * HTTP 500 Internal Server Error.
     * Logs full stack trace for debugging while hiding details from client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return handleExceptionInternal(
                ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Converts camelCase to snake_case for field names.
     * Example: "userName" -> "user_name"
     */
    private String convertToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
