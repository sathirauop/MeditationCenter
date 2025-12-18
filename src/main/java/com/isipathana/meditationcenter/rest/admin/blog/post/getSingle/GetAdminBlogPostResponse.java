package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for GET /api/admin/blog/{postId} endpoint.
 * Contains full blog post details for admin editing interface.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminBlogPostResponse(
        @JsonProperty("post_id")
        Long postId,

        String title,

        String excerpt,

        String content,

        @JsonProperty("title_si")
        String titleSi,

        @JsonProperty("excerpt_si")
        String excerptSi,

        @JsonProperty("content_si")
        String contentSi,

        String slug,

        @JsonProperty("author_id")
        Long authorId,

        @JsonProperty("author_name")
        String authorName,

        @JsonProperty("cover_image_key")
        String coverImageKey,

        @JsonProperty("gallery_image_keys")
        Set<String> galleryImageKeys,

        BlogPostStatus status,

        @JsonProperty("published_at")
        LocalDateTime publishedAt,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription,

        @JsonProperty("view_count")
        Long viewCount,

        @JsonProperty("tag_ids")
        Set<Long> tagIds,

        Long version,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) implements ApiResponse {}
