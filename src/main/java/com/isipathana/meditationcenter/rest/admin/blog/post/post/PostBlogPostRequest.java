package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request for creating a new blog post.
 *
 * @author Sathira Basnayake
 */
public record PostBlogPostRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        @Size(max = 500, message = "Excerpt must not exceed 500 characters")
        String excerpt,

        @NotBlank(message = "Content is required")
        @Size(min = 10, message = "Content must be at least 10 characters")
        String content,

        // Sinhala fields (optional)
        @Size(min = 3, max = 255, message = "Sinhala title must be between 3 and 255 characters")
        String titleSi,

        @Size(max = 500, message = "Sinhala excerpt must not exceed 500 characters")
        String excerptSi,

        @Size(min = 10, message = "Sinhala content must be at least 10 characters")
        String contentSi,

        // Optional slug (auto-generated if not provided)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Invalid slug format (lowercase, hyphens only)")
        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        // SEO (optional)
        @Size(max = 255, message = "Meta title must not exceed 255 characters")
        String metaTitle,

        @Size(max = 500, message = "Meta description must not exceed 500 characters")
        String metaDescription,

        // Tag IDs (optional)
        Set<Long> tagIds,

        // Status (defaults to DRAFT in UseCase if not provided)
        BlogPostStatus status
) {
}
