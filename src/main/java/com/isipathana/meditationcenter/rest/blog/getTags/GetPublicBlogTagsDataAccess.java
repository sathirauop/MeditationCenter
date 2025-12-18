package com.isipathana.meditationcenter.rest.blog.getTags;

import java.util.stream.Stream;

/**
 * Data access interface for fetching public blog tags.
 * Only counts PUBLISHED posts (not drafts).
 *
 * @author Sathira Basnayake
 */
public interface GetPublicBlogTagsDataAccess {

    /**
     * Fetches all tags with published post counts.
     *
     * @return Stream of tags with post counts
     */
    Stream<PublicBlogTagWithCount> findAllTagsWithPublishedCount();
}
