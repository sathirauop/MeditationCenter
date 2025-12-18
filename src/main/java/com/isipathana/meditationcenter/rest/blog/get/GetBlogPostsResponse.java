package com.isipathana.meditationcenter.rest.blog.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for GET /api/blog endpoint.
 * Contains essential blog post information for listing published posts.
 * <p>
 * Image URLs are presigned URLs with 5-minute expiry for secure temporary access.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetBlogPostsResponse(
        @JsonProperty("post_id")
        Long postId,

        String title,

        String excerpt,

        @JsonProperty("title_si")
        String titleSi,

        @JsonProperty("excerpt_si")
        String excerptSi,

        String slug,

        @JsonProperty("author_name")
        String authorName,

        @JsonProperty("cover_image_url")
        String coverImageUrl,

        @JsonProperty("published_at")
        LocalDateTime publishedAt,

        @JsonProperty("view_count")
        Long viewCount,

        @JsonProperty("tag_names")
        Set<String> tagNames
) implements ApiResponse {}
