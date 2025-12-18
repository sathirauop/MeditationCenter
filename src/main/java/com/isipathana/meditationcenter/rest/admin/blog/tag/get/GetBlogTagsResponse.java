package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response for a single blog tag in the list.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetBlogTagsResponse(
        @JsonProperty("tag_id")
        Long tagId,

        String name,

        @JsonProperty("name_si")
        String nameSi,

        String slug,

        @JsonProperty("post_count")
        Long postCount,  // Number of posts using this tag

        @JsonProperty("created_at")
        LocalDateTime createdAt
) implements ApiResponse {
}
