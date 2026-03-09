package com.isipathana.meditationcenter.rest.admin.program.patch;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HTTP data access interface for generating presigned URLs for program images
 * (PATCH).
 *
 * @author Sathira Basnayake
 */
public interface PatchProgramHttpDataAccess {

    /**
     * Generates a presigned URL for a single image key.
     *
     * @param imageKey The R2 image key
     * @return The presigned URL
     */
    String generatePresignedUrl(String imageKey);

    /**
     * Generates presigned URLs for multiple image keys.
     *
     * @param imageKeys The R2 image keys
     * @return Map of image key to presigned URL
     */
    Map<String, String> generatePresignedUrls(Set<String> imageKeys);

    /**
     * Uploads a cover image to R2.
     *
     * @param programId  The program ID
     * @param coverImage The cover image file
     * @return The R2 key of the uploaded image
     */
    String uploadCoverImage(Long programId, MultipartFile coverImage);

    /**
     * Uploads multiple gallery images to R2.
     *
     * @param programId     The program ID
     * @param galleryImages The gallery image files
     * @return List of R2 keys of the uploaded images
     */
    List<String> uploadGalleryImages(Long programId, List<MultipartFile> galleryImages);

    /**
     * Deletes a file from R2.
     *
     * @param imageKey The R2 image key to delete
     * @return true if deletion was successful
     */
    boolean deleteImage(String imageKey);
}
