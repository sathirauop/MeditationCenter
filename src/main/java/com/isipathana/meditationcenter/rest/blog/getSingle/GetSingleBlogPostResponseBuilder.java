package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;

/**
 * Response builder interface for GET /api/blog/{slug} endpoint.
 * Transforms blog post domain object to API response DTO.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleBlogPostResponseBuilder {

    /**
     * Builds response from blog post domain object.
     *
     * @param post Blog post with tags
     * @return Response DTO
     */
    GetSingleBlogPostResponse build(BlogPostWithTags post);
}
