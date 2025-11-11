package com.isipathana.meditationcenter.rest.admin.event.post;

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
 * Repository implementation for uploading event images to R2 storage.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Claude Code
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class PostEventHttpRepository implements PostEventHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(PostEventHttpRepository.class);

    private final R2FileManagerClient r2FileManagerClient;
    private final ImageValidationService imageValidationService;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String uploadCoverImage(Long eventId, MultipartFile coverImage) {
        if (coverImage == null || coverImage.isEmpty()) {
            logger.debug("No cover image provided for event {}", eventId);
            return null;
        }

        // Validate image
        ImageValidationService.ValidationResult validationResult = imageValidationService.validate(coverImage);
        if (!validationResult.valid()) {
            logger.error("Cover image validation failed for event {}: {}", eventId, validationResult.errorMessage());
            throw new IllegalArgumentException("Invalid cover image: " + validationResult.errorMessage());
        }

        // Generate unique key
        String key = generateImageKey(eventId, "cover", coverImage.getOriginalFilename());

        // Get bucket name
        String bucketName = r2ClientProperties.buckets().get("events").bucketName();

        // Upload to R2
        boolean success = r2FileManagerClient.uploadFile(bucketName, key, coverImage);

        if (!success) {
            logger.error("Failed to upload cover image to R2 for event {}", eventId);
            throw new RuntimeException("Failed to upload cover image to R2");
        }

        logger.info("Successfully uploaded cover image for event {}: {}", eventId, key);
        return key;
    }

    @Override
    public List<String> uploadGalleryImages(Long eventId, List<MultipartFile> galleryImages) {
        if (galleryImages == null || galleryImages.isEmpty()) {
            logger.debug("No gallery images provided for event {}", eventId);
            return List.of();
        }

        List<String> uploadedKeys = new ArrayList<>();
        String bucketName = r2ClientProperties.buckets().get("events").bucketName();

        for (int i = 0; i < galleryImages.size(); i++) {
            MultipartFile image = galleryImages.get(i);

            if (image.isEmpty()) {
                logger.warn("Skipping empty gallery image at index {} for event {}", i, eventId);
                continue;
            }

            // Validate image
            ImageValidationService.ValidationResult validationResult = imageValidationService.validate(image);
            if (!validationResult.valid()) {
                logger.error("Gallery image validation failed at index {} for event {}: {}",
                        i, eventId, validationResult.errorMessage());
                throw new IllegalArgumentException(
                        String.format("Invalid gallery image at index %d: %s", i, validationResult.errorMessage())
                );
            }

            // Generate unique key
            String key = generateImageKey(eventId, "gallery", image.getOriginalFilename());

            // Upload to R2
            boolean success = r2FileManagerClient.uploadFile(bucketName, key, image);

            if (!success) {
                logger.error("Failed to upload gallery image at index {} to R2 for event {}", i, eventId);
                throw new RuntimeException(
                        String.format("Failed to upload gallery image at index %d to R2", i)
                );
            }

            uploadedKeys.add(key);
            logger.info("Successfully uploaded gallery image {} for event {}: {}", i, eventId, key);
        }

        logger.info("Successfully uploaded {} gallery images for event {}", uploadedKeys.size(), eventId);
        return uploadedKeys;
    }

    /**
     * Generates a unique R2 object key for an image.
     * <p>
     * Format: events/{eventId}/{type}/{uuid}.{extension}
     * Example: events/123/cover/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
     *
     * @param eventId  The event ID
     * @param type     The image type ("cover" or "gallery")
     * @param filename The original filename
     * @return The generated R2 object key
     */
    private String generateImageKey(Long eventId, String type, String filename) {
        String extension = extractFileExtension(filename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("events/%d/%s/%s.%s", eventId, type, uniqueId, extension);
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
