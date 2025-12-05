package com.isipathana.meditationcenter.rest.admin.event.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data access interface for event image upload operations to R2 storage.
 * <p>
 * Handles uploading event images (cover and gallery) to Cloudflare R2.
 *
 * @author Claude Code
 */
public interface PostEventHttpDataAccess {

    /**
     * Uploads a cover image for an event to R2.
     *
     * @param eventId   The ID of the event
     * @param coverImage The cover image file to upload
     * @return The R2 object key (path) of the uploaded image, or null if upload failed
     */
    String uploadCoverImage(Long eventId, MultipartFile coverImage);

    /**
     * Uploads multiple gallery images for an event to R2.
     *
     * @param eventId       The ID of the event
     * @param galleryImages List of gallery image files to upload
     * @return List of R2 object keys (paths) for successfully uploaded images
     */
    List<String> uploadGalleryImages(Long eventId, List<MultipartFile> galleryImages);
}
