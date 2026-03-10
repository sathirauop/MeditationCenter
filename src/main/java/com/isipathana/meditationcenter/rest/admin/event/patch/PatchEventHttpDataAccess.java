package com.isipathana.meditationcenter.rest.admin.event.patch;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data access interface for event image update operations to R2 storage.
 * <p>
 * Handles uploading replacement event images (cover and gallery) to Cloudflare
 * R2
 * and deleting old images when they are replaced.
 *
 * @author Sathira Basnayake
 */
public interface PatchEventHttpDataAccess {

    /**
     * Uploads a new cover image for an event to R2.
     * Does NOT delete the old cover image — caller must handle that.
     *
     * @param eventId    The ID of the event
     * @param coverImage The new cover image file to upload
     * @return The R2 object key (path) of the uploaded image, or null if upload
     *         failed
     */
    String uploadCoverImage(Long eventId, MultipartFile coverImage);

    /**
     * Uploads new gallery images for an event to R2.
     * Does NOT delete old gallery images — caller must handle that.
     *
     * @param eventId       The ID of the event
     * @param galleryImages List of new gallery image files to upload
     * @return List of R2 object keys (paths) for successfully uploaded images
     */
    List<String> uploadGalleryImages(Long eventId, List<MultipartFile> galleryImages);

    /**
     * Deletes a single image from R2 by its key.
     *
     * @param imageKey The R2 object key to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteImage(String imageKey);
}
