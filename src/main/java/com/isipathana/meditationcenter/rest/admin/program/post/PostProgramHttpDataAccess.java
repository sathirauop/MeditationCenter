package com.isipathana.meditationcenter.rest.admin.program.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * HTTP data access interface for uploading program images to R2.
 *
 * @author Sathira Basnayake
 */
public interface PostProgramHttpDataAccess {

    /**
     * Uploads a cover image to R2.
     *
     * @param programId The program ID
     * @param coverImage The cover image file
     * @return The R2 key of the uploaded image
     */
    String uploadCoverImage(Long programId, MultipartFile coverImage);

    /**
     * Uploads multiple gallery images to R2.
     *
     * @param programId The program ID
     * @param galleryImages The gallery image files
     * @return List of R2 keys of the uploaded images
     */
    List<String> uploadGalleryImages(Long programId, List<MultipartFile> galleryImages);
}
