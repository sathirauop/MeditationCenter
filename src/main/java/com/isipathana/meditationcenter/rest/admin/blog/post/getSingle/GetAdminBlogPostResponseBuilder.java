package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPost;

/**
 * Response builder interface for GET /api/admin/blog/{postId} endpoint.
 * Transforms blog post domain object to admin response DTO.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminBlogPostResponseBuilder {

    /**
     * Builds response from blog post domain object.
     *
     * @param post Blog post
     * @return Response DTO
     */
    GetAdminBlogPostResponse build(BlogPost post);
}
