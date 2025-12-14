package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating book fields (partial update).
 * All fields are optional - only provided fields will be updated.
 *
 * @author Sathira Basnayake
 */
public record PatchBookRequest(
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
        String author,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @JsonProperty("is_active")
        Boolean isActive
) {
}
