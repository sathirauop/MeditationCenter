package com.isipathana.meditationcenter.rest.admin.book.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new book.
 *
 * @author Sathira Basnayake
 */
public record PostBookRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 255, message = "Author must not exceed 255 characters")
        String author,

        String description
) {}
