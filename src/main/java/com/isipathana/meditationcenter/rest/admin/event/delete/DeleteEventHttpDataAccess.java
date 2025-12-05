package com.isipathana.meditationcenter.rest.admin.event.delete;

import java.util.Set;

/**
 * Data access interface for event image deletion operations from R2 storage.
 * <p>
 * Handles deleting event images (cover and gallery) from Cloudflare R2.
 *
 * @author Sathira Basnayake
 */
public interface DeleteEventHttpDataAccess {

    /**
     * Deletes the cover image for an event from R2.
     * <p>
     * Removes the specified image file from R2 storage.
     *
     * @param coverImageKey The R2 object key (path) of the cover image to delete
     * @return true if deletion successful or image doesn't exist, false if deletion failed
     */
    boolean deleteCoverImage(String coverImageKey);

    /**
     * Deletes all gallery images for an event from R2.
     * <p>
     * Removes multiple image files from R2 storage.
     *
     * @param galleryImageKeys Set of R2 object keys (paths) for gallery images to delete
     * @return true if all deletions successful, false if any failed
     */
    boolean deleteGalleryImages(Set<String> galleryImageKeys);

    /**
     * Deletes all images for an event (both cover and gallery) by removing the entire event folder.
     * <p>
     * This is more efficient than deleting individual images as it removes the entire
     * events/{eventId}/ folder in a single batch operation.
     *
     * @param eventId The ID of the event whose folder should be deleted
     * @return true if deletion successful, false if deletion failed
     */
    boolean deleteEventFolder(Long eventId);
}
