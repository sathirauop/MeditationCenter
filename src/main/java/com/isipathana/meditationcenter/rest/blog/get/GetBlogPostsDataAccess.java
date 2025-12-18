package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;

import java.util.stream.Stream;

/**
 * Data access interface for fetching published blog posts.
 *
 * @author Sathira Basnayake
 */
public interface GetBlogPostsDataAccess {

    /**
     * Fetches published blog posts with filters and pagination.
     *
     * @param request Filter and pagination parameters
     * @return Stream of blog posts with tag names
     */
    Stream<BlogPostWithTags> getPublishedBlogPosts(GetBlogPostsRequest request);

    /**
     * Counts total published blog posts matching the filters.
     *
     * @param request Filter parameters
     * @return Total count of matching posts
     */
    long countPublishedBlogPosts(GetBlogPostsRequest request);
}
