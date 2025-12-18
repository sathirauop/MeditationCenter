package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import com.isipathana.meditationcenter.records.blog.BlogTag;

import java.util.Optional;

/**
 * Data access interface for updating blog tags.
 *
 * @author Sathira Basnayake
 */
public interface PatchBlogTagDataAccess {
    Optional<BlogTag> findTagById(Long tagId);
    BlogTag updateTag(BlogTag tag);
}
