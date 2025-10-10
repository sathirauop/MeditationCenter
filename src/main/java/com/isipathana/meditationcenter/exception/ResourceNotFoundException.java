package com.isipathana.meditationcenter.exception;

/**
 * Represents an exception that occurs when a requested resource is not found.
 * Returns an HTTP 404 Not Found status code.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, Long id) {
        this(entityName, String.valueOf(id));
    }

    public ResourceNotFoundException(String entityName, Integer id) {
        this(entityName, String.valueOf(id));
    }

    public ResourceNotFoundException(String entityName, String id) {
        super(String.format("%s with id %s not found", entityName, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
