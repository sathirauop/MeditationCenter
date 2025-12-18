package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response for blog post creation.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PostBlogPostResponse(
        @JsonProperty("post_id")
        Long postId,

        String title,

        @JsonProperty("title_si")
        String titleSi,

        String slug,

        String excerpt,

        @JsonProperty("excerpt_si")
        String excerptSi,

        @JsonProperty("author_id")
        Long authorId,

        @JsonProperty("cover_image_key")
        String coverImageKey,

        @JsonProperty("gallery_image_keys")
        Set<String> galleryImageKeys,

        BlogPostStatus status,

        @JsonProperty("published_at")
        LocalDateTime publishedAt,

        @JsonProperty("tag_ids")
        Set<Long> tagIds,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) implements ApiResponse {
}
