package com.isipathana.meditationcenter.rest.book.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request for retrieving paginated list of active books.
 *
 * @author Sathira Basnayake
 */
public record GetBooksRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset
) {
    /**
     * Default constructor with standard pagination values.
     */
    public GetBooksRequest() {
        this(20, 0);
    }
}
