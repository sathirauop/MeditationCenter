package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * HTTP data access interface for blog post image uploads.
 * Handles R2/S3 storage operations.
 *
 * @author Sathira Basnayake
 */
public interface PostBlogPostHttpDataAccess {

    /**
     * Uploads a cover image to R2 storage.
     *
     * @param postId The blog post ID
     * @param file   The cover image file
     * @return The R2 storage key
     */
    String uploadCoverImage(Long postId, MultipartFile file);

    /**
     * Uploads gallery images to R2 storage.
     *
     * @param postId The blog post ID
     * @param files  The gallery image files
     * @return List of R2 storage keys
     */
    List<String> uploadGalleryImages(Long postId, List<MultipartFile> files);
}
