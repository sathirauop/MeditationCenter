package com.isipathana.meditationcenter.rest.admin.gallery.deletePhoto;

/**
 * Data access interface for deleting gallery photos.
 *
 * @author Sathira Basnayake
 */
public interface DeleteGalleryPhotoDataAccess {

    /**
     * Checks if a photo belongs to the specified group.
     *
     * @param groupId The group ID
     * @param photoId The photo ID
     * @return true if the photo exists and belongs to the group
     */
    boolean photoExistsInGroup(Long groupId, Long photoId);

    /**
     * Deletes a single photo.
     *
     * @param photoId The photo ID
     */
    void deletePhoto(Long photoId);
}
