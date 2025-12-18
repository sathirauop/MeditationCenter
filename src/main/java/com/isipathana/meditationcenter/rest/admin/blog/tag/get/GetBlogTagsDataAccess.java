package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import java.util.stream.Stream;

/**
 * Data access interface for retrieving blog tags.
 *
 * @author Sathira Basnayake
 */
public interface GetBlogTagsDataAccess {

    /**
     * Retrieves all blog tags with post counts.
     *
     * @return Stream of blog tags with post counts
     */
    Stream<BlogTagWithCount> findAllTagsWithCount();
}
