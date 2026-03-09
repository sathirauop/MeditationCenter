package com.isipathana.meditationcenter.rest.admin.gallery.delete;

/**
 * Data access interface for deleting gallery groups.
 *
 * @author Sathira Basnayake
 */
public interface DeleteGalleryGroupDataAccess {

    /**
     * Checks if a gallery group exists.
     *
     * @param groupId The group ID
     * @return true if exists
     */
    boolean groupExists(Long groupId);

    /**
     * Deletes a gallery group and all its photos (cascade).
     *
     * @param groupId The group ID
     */
    void deleteGroup(Long groupId);
}
