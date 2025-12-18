package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request for creating a new blog tag.
 *
 * @author Sathira Basnayake
 */
public record PostBlogTagRequest(
        @NotBlank(message = "Tag name is required")
        @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
        String name,

        @Size(min = 2, max = 50, message = "Sinhala tag name must be between 2 and 50 characters")
        String nameSi,

        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Invalid slug format (lowercase, hyphens only)")
        @Size(max = 50, message = "Slug must not exceed 50 characters")
        String slug  // Optional - auto-generated from name if not provided
) {
}
