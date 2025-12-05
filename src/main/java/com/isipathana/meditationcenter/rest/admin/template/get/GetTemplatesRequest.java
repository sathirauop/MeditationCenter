package com.isipathana.meditationcenter.rest.admin.template.get;

/**
 * Request DTO for retrieving paginated templates.
 *
 * @author Sathira Basnayake
 */
public record GetTemplatesRequest(
        int limit,
        int offset
) {
    public GetTemplatesRequest {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
    }
}
