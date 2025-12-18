package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import jakarta.validation.constraints.Size;

/**
 * Request for updating a blog tag.
 * All fields are optional - only provided fields will be updated.
 *
 * @author Sathira Basnayake
 */
public record PatchBlogTagRequest(
        @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
        String name,

        @Size(min = 2, max = 50, message = "Sinhala tag name must be between 2 and 50 characters")
        String nameSi
) {
}
