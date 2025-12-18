package com.isipathana.meditationcenter.rest.admin.blog.tag.delete;

/**
 * Data access interface for deleting blog tags.
 *
 * @author Sathira Basnayake
 */
public interface DeleteBlogTagDataAccess {
    boolean existsById(Long tagId);
    void deleteTag(Long tagId);
}
