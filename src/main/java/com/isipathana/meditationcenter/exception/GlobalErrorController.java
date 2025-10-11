package com.isipathana.meditationcenter.exception;

import com.isipathana.meditationcenter.exception.response.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fallback error handler for errors that occur outside Spring MVC's exception handling scope.
 * <p>
 * This controller handles errors that GlobalExceptionHandler cannot catch:
 * - 404 Not Found (no controller mapping exists)
 * - 405 Method Not Allowed (wrong HTTP method)
 * - Filter chain exceptions (before reaching controllers)
 * - Servlet container errors
 * - Static resource errors
 * <p>
 * Works in conjunction with GlobalExceptionHandler:
 * - GlobalExceptionHandler: Catches exceptions from controllers (95% of errors)
 * - GlobalErrorController: Safety net for everything else (5% of errors)
 */
@RestController
@RequestMapping("/error")
@RequiredArgsConstructor
public class GlobalErrorController implements ErrorController {

    private final Logger logger;

    /**
     * Handles all error requests forwarded to /error endpoint.
     * Spring Boot automatically forwards unhandled errors here.
     *
     * @param request The servlet request containing error attributes
     * @return ResponseEntity with appropriate ErrorResponse and HTTP status
     */
    @RequestMapping
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        HttpStatus status;
        String message;
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        try {
            // Extract status code from request attributes
            Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            status = statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;

            // Extract error message
            message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if (message == null || message.isEmpty()) {
                message = status.getReasonPhrase();
            }

            // Get exception if available for better logging
            Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            if (exception != null) {
                logger.error("Error at {}: {} - {}", requestUri, status.value(), message, exception);
            }

        } catch (Exception e) {
            // Fallback if error attribute extraction fails
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal Server Error";
            logger.error("Error processing error request at {}", requestUri, e);
        }

        // Log based on error severity
        if (status.is5xxServerError()) {
            logger.error("Server error [{}]: {} - {}", status.value(), requestUri, message);
        } else if (status.is4xxClientError()) {
            logger.warn("Client error [{}]: {} - {}", status.value(), requestUri, message);
        }

        // Create appropriate ErrorResponse based on status code
        ErrorResponse errorResponse = createErrorResponse(status, message);

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Creates the appropriate ErrorResponse implementation based on HTTP status.
     * Uses generic security-conscious messages for sensitive status codes.
     *
     * @param status  The HTTP status code
     * @param message The error message
     * @return Appropriate ErrorResponse implementation
     */
    private ErrorResponse createErrorResponse(HttpStatus status, String message) {
        return switch (status.value()) {
            case 400 -> new BadRequestErrorResponse(message);
            case 401 -> new AuthenticationErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            case 403 -> new ForbiddenErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase());
            case 404 -> new NotFoundErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase());
            case 405 -> new BadRequestErrorResponse("Method Not Allowed");
            case 409 -> new ConflictErrorResponse(message);
            case 422 -> new UnprocessableEntityErrorResponse(null, message);
            case 500 -> new InternalServerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            case 503 -> new InternalServerErrorResponse("Service Unavailable");
            default -> {
                if (status.is4xxClientError()) {
                    yield new BadRequestErrorResponse(message);
                } else {
                    yield new InternalServerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
                }
            }
        };
    }
}
