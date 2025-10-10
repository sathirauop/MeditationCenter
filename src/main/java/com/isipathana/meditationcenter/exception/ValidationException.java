package com.isipathana.meditationcenter.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Getter;

/**
 * Represents an exception that occurs when a validation error is detected.
 * Returns an HTTP 400 Bad Request status code.
 */
@Getter
public class ValidationException extends RuntimeException {
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public ValidationException(String message) {
        this(null, message);
    }

    public static ValidationException fromException(Exception e) {
        return switch (e) {
            case MismatchedInputException ex -> fromMismatchedInputException(ex);
            case IllegalArgumentException ex -> new ValidationException(ex.getMessage());
            default -> new ValidationException(e.getMessage());
        };
    }

    private static ValidationException fromMismatchedInputException(MismatchedInputException ex) {
        StringBuilder fieldBuilder = new StringBuilder();

        for (JsonMappingException.Reference path : ex.getPath()) {
            fieldBuilder.append(
                path.getFieldName().isEmpty() ? String.valueOf(path.getIndex()) : path.getFieldName());
            fieldBuilder.append(".");
        }

        return new ValidationException(fieldBuilder.toString(), ex.getMessage());
    }
}
