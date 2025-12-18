package com.isipathana.meditationcenter.rest.blog.get;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request DTO for GET /api/blog endpoint.
 * Supports offset-based pagination and filtering for published blog posts.
 *
 * @param limit      Maximum number of posts to return (1-100, default: 20)
 * @param offset     Page offset for pagination (0-based, default: 0)
 * @param tagIds     Filter by tag IDs (optional)
 * @param search     Search query for title/content (optional)
 * @param startDate  Filter posts published after this date (optional)
 * @param endDate    Filter posts published before this date (optional)
 * @param sortBy     Sort order: NEWEST, OLDEST, MOST_VIEWED (default: NEWEST)
 * @author Sathira Basnayake
 */
public record GetBlogPostsRequest(
        @Min(1) @Max(100) Integer limit,
        @Min(0) Integer offset,
        Set<Long> tagIds,
        String search,
        LocalDate startDate,
        LocalDate endDate,
        SortOrder sortBy
) {
    /**
     * Compact constructor to apply default values for null parameters.
     */
    public GetBlogPostsRequest {
        limit = (limit == null) ? 20 : limit;
        offset = (offset == null) ? 0 : offset;
        sortBy = (sortBy == null) ? SortOrder.NEWEST : sortBy;
    }

    /**
     * Sort order options for blog posts.
     */
    public enum SortOrder {
        NEWEST,      // publishedAt DESC
        OLDEST,      // publishedAt ASC
        MOST_VIEWED  // viewCount DESC, publishedAt DESC
    }
}
