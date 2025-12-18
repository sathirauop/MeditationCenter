package com.isipathana.meditationcenter.rest.admin.blog.post.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for GET /api/admin/blog endpoint.
 * Contains blog post information for admin dashboard (includes drafts).
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminBlogPostsResponse(
        @JsonProperty("post_id")
        Long postId,

        String title,

        @JsonProperty("title_si")
        String titleSi,

        String slug,

        @JsonProperty("author_id")
        Long authorId,

        @JsonProperty("author_name")
        String authorName,

        BlogPostStatus status,

        @JsonProperty("published_at")
        LocalDateTime publishedAt,

        @JsonProperty("view_count")
        Long viewCount,

        @JsonProperty("tag_names")
        Set<String> tagNames,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) implements ApiResponse {}
