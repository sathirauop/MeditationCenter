package com.isipathana.meditationcenter.rest.admin.blog.post.get;

import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;

/**
 * Request DTO for GET /api/admin/blog endpoint.
 * Supports offset-based pagination and filtering for all blog posts (admin view).
 *
 * @param limit      Maximum number of posts to return (1-100)
 * @param offset     Page offset for pagination (0-based)
 * @param status     Filter by post status (optional - DRAFT, PUBLISHED)
 * @param authorId   Filter by author ID (optional)
 * @param tagIds     Filter by tag IDs (optional)
 * @param search     Search query for title/content (optional)
 * @author Sathira Basnayake
 */
public record GetAdminBlogPostsRequest(
        @Min(1) @Max(100) int limit,
        @Min(0) int offset,
        BlogPostStatus status,
        Long authorId,
        Set<Long> tagIds,
        String search
) {
    /**
     * Default constructor with sensible defaults.
     */
    public GetAdminBlogPostsRequest() {
        this(20, 0, null, null, null, null);
    }
}
