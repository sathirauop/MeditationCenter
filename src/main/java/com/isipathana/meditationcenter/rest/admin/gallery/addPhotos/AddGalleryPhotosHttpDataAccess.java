package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * HTTP data access interface for gallery photo uploads.
 * Handles R2/S3 storage operations.
 *
 * @author Sathira Basnayake
 */
public interface AddGalleryPhotosHttpDataAccess {

    /**
     * Uploads gallery photos to R2 storage.
     *
     * @param groupId The gallery group ID
     * @param files   The photo files
     * @return List of R2 storage keys
     */
    List<String> uploadPhotos(Long groupId, List<MultipartFile> files);
}
