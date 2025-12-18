package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;

import java.util.stream.Stream;

/**
 * Response builder interface for GET /api/blog endpoint.
 * Transforms blog post domain objects to API response DTOs.
 *
 * @author Sathira Basnayake
 */
public interface GetBlogPostsResponseBuilder {

    /**
     * Builds paginated response from blog posts.
     *
     * @param posts         Stream of blog posts with tags
     * @param currentOffset Current pagination offset
     * @param maxOffset     Total count of matching posts
     * @return Paginated response
     */
    OffsetSearchResponse<GetBlogPostsResponse> build(
            Stream<BlogPostWithTags> posts,
            long currentOffset,
            long maxOffset
    );
}
