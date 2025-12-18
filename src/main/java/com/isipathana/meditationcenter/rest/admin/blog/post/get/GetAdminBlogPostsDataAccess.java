package com.isipathana.meditationcenter.rest.admin.blog.post.get;

import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;

import java.util.stream.Stream;

/**
 * Data access interface for fetching all blog posts (admin view).
 * Includes drafts and unpublished posts.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminBlogPostsDataAccess {

    /**
     * Fetches all blog posts with filters and pagination.
     * Includes drafts and all statuses.
     *
     * @param request Filter and pagination parameters
     * @return Stream of blog posts with tag names
     */
    Stream<BlogPostWithTags> getAllBlogPosts(GetAdminBlogPostsRequest request);

    /**
     * Counts total blog posts matching the filters.
     *
     * @param request Filter parameters
     * @return Total count of matching posts
     */
    long countAllBlogPosts(GetAdminBlogPostsRequest request);
}
