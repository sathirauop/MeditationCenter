package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.isipathana.meditationcenter.records.blog.BlogTag;

/**
 * Data access interface for creating blog tags.
 *
 * @author Sathira Basnayake
 */
public interface PostBlogTagDataAccess {

    /**
     * Creates a new blog tag in the database.
     *
     * @param tag The blog tag to create
     * @return The created blog tag with generated ID and timestamp
     */
    BlogTag createTag(BlogTag tag);

    /**
     * Checks if a tag with the given slug already exists.
     *
     * @param slug The slug to check
     * @return true if a tag with this slug exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Checks if a tag with the given name already exists.
     *
     * @param name The name to check
     * @return true if a tag with this name exists, false otherwise
     */
    boolean existsByName(String name);
}
