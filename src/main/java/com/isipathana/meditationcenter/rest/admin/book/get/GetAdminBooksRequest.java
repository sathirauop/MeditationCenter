package com.isipathana.meditationcenter.rest.admin.book.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request for retrieving paginated list of all books (admin view).
 *
 * @author Sathira Basnayake
 */
public record GetAdminBooksRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset
) {
    /**
     * Default constructor with standard pagination values.
     */
    public GetAdminBooksRequest() {
        this(20, 0);
    }
}
