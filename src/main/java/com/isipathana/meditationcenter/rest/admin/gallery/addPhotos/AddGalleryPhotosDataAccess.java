package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;

/**
 * Data access interface for adding photos to gallery groups.
 *
 * @author Sathira Basnayake
 */
public interface AddGalleryPhotosDataAccess {

    /**
     * Checks if a gallery group exists.
     *
     * @param groupId The group ID
     * @return true if exists
     */
    boolean groupExists(Long groupId);

    /**
     * Gets the current max sort order for photos in a group.
     *
     * @param groupId The group ID
     * @return The max sort order, or 0 if no photos exist
     */
    int getMaxSortOrder(Long groupId);

    /**
     * Creates a new photo record.
     *
     * @param photo The photo to create
     * @return The created photo with generated ID
     */
    GalleryPhoto createPhoto(GalleryPhoto photo);
}
