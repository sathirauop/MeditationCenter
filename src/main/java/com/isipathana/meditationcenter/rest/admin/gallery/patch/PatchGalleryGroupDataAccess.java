package com.isipathana.meditationcenter.rest.admin.gallery.patch;

/**
 * Data access interface for updating gallery groups.
 *
 * @author Sathira Basnayake
 */
public interface PatchGalleryGroupDataAccess {

    /**
     * Updates a gallery group partially.
     *
     * @param groupId The group ID
     * @param request The update request
     * @return true if the group was found and updated
     */
    boolean updateGroup(Long groupId, PatchGalleryGroupRequest request);
}
