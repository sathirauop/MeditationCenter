package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.service.ImageValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository implementation for uploading blog post images to R2 storage.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class PostBlogPostHttpRepository implements PostBlogPostHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(PostBlogPostHttpRepository.class);

    private final R2FileManagerClient r2FileManagerClient;
    private final ImageValidationService imageValidationService;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String uploadCoverImage(Long postId, MultipartFile coverImage) {
        if (coverImage == null || coverImage.isEmpty()) {
            logger.debug("No cover image provided for blog post {}", postId);
            return null;
        }

        // Validate image
        ImageValidationService.ValidationResult validationResult = imageValidationService.validate(coverImage);
        if (!validationResult.valid()) {
            logger.error("Cover image validation failed for blog post {}: {}", postId, validationResult.errorMessage());
            throw new IllegalArgumentException("Invalid cover image: " + validationResult.errorMessage());
        }

        // Generate unique key
        String key = generateImageKey(postId, "cover", coverImage.getOriginalFilename());

        // Get bucket name for blog posts
        String bucketName = r2ClientProperties.buckets().get("blog-posts").bucketName();

        // Upload to R2
        boolean success = r2FileManagerClient.uploadFile(bucketName, key, coverImage);

        if (!success) {
            logger.error("Failed to upload cover image to R2 for blog post {}", postId);
            throw new RuntimeException("Failed to upload cover image to R2");
        }

        logger.info("Successfully uploaded cover image for blog post {}: {}", postId, key);
        return key;
    }

    @Override
    public List<String> uploadGalleryImages(Long postId, List<MultipartFile> galleryImages) {
        if (galleryImages == null || galleryImages.isEmpty()) {
            logger.debug("No gallery images provided for blog post {}", postId);
            return List.of();
        }

        List<String> uploadedKeys = new ArrayList<>();
        String bucketName = r2ClientProperties.buckets().get("blog-posts").bucketName();

        for (int i = 0; i < galleryImages.size(); i++) {
            MultipartFile image = galleryImages.get(i);

            if (image.isEmpty()) {
                logger.warn("Skipping empty gallery image at index {} for blog post {}", i, postId);
                continue;
            }

            // Validate image
            ImageValidationService.ValidationResult validationResult = imageValidationService.validate(image);
            if (!validationResult.valid()) {
                logger.error("Gallery image validation failed at index {} for blog post {}: {}",
                        i, postId, validationResult.errorMessage());
                throw new IllegalArgumentException(
                        String.format("Invalid gallery image at index %d: %s", i, validationResult.errorMessage())
                );
            }

            // Generate unique key
            String key = generateImageKey(postId, "gallery", image.getOriginalFilename());

            // Upload to R2
            boolean success = r2FileManagerClient.uploadFile(bucketName, key, image);

            if (!success) {
                logger.error("Failed to upload gallery image at index {} to R2 for blog post {}", i, postId);
                throw new RuntimeException(
                        String.format("Failed to upload gallery image at index %d to R2", i)
                );
            }

            uploadedKeys.add(key);
            logger.info("Successfully uploaded gallery image {} for blog post {}: {}", i, postId, key);
        }

        logger.info("Successfully uploaded {} gallery images for blog post {}", uploadedKeys.size(), postId);
        return uploadedKeys;
    }

    /**
     * Generates a unique R2 object key for an image.
     * <p>
     * Format: blog-posts/{postId}/{type}/{uuid}.{extension}
     * Example: blog-posts/123/cover/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
     *
     * @param postId   The blog post ID
     * @param type     The image type ("cover" or "gallery")
     * @param filename The original filename
     * @return The generated R2 object key
     */
    private String generateImageKey(Long postId, String type, String filename) {
        String extension = extractFileExtension(filename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("blog-posts/%d/%s/%s.%s", postId, type, uniqueId, extension);
    }

    /**
     * Extracts file extension from filename.
     * Defaults to "jpg" if no extension found.
     */
    private String extractFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "jpg";
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
