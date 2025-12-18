package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for GET /api/blog/{slug} endpoint.
 * Contains full blog post details including content and gallery images.
 * <p>
 * Image URLs are presigned URLs with 5-minute expiry for secure temporary access.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetSingleBlogPostResponse(
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

        @JsonProperty("author_name")
        String authorName,

        @JsonProperty("cover_image_url")
        String coverImageUrl,

        @JsonProperty("gallery_image_urls")
        Set<String> galleryImageUrls,

        @JsonProperty("published_at")
        LocalDateTime publishedAt,

        @JsonProperty("view_count")
        Long viewCount,

        @JsonProperty("tag_names")
        Set<String> tagNames,

        @JsonProperty("meta_title")
        String metaTitle,

        @JsonProperty("meta_description")
        String metaDescription
) implements ApiResponse {}
