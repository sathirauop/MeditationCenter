package com.isipathana.meditationcenter.rest.blog.getTags;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

/**
 * Response DTO for GET /api/blog/tags endpoint (public).
 * Contains tag information with published post counts only.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetPublicBlogTagsResponse(
        @JsonProperty("tag_id")
        Long tagId,

        String name,

        @JsonProperty("name_si")
        String nameSi,

        String slug,

        @JsonProperty("post_count")
        Long postCount  // Only counts PUBLISHED posts
) implements ApiResponse {}
