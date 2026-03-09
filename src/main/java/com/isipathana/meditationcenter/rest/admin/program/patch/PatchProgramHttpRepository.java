package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.service.ImageValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;

/**
 * Repository implementation for generating presigned URLs and uploading program
 * images (PATCH).
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class PatchProgramHttpRepository implements PatchProgramHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(PatchProgramHttpRepository.class);

    private final R2PresignerClient r2PresignerClient;
    private final R2FileManagerClient r2FileManagerClient;
    private final ImageValidationService imageValidationService;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String generatePresignedUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            logger.debug("No image key provided, returning null");
            return null;
        }

        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("programs").presignedUrlExpiry();

        try {
            String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, imageKey, expiry);
            logger.debug("Generated presigned URL for image key: {}", imageKey);
            return presignedUrl;
        } catch (Exception e) {
            logger.error("Failed to generate presigned URL for image key: {}", imageKey, e);
            return null;
        }
    }

    @Override
    public Map<String, String> generatePresignedUrls(Set<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            logger.debug("No image keys provided, returning empty map");
            return Map.of();
        }

        Map<String, String> urlMap = new HashMap<>();
        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("programs").presignedUrlExpiry();

        for (String imageKey : imageKeys) {
            try {
                String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, imageKey, expiry);
                urlMap.put(imageKey, presignedUrl);
                logger.debug("Generated presigned URL for image key: {}", imageKey);
            } catch (Exception e) {
                logger.error("Failed to generate presigned URL for image key: {}", imageKey, e);
            }
        }

        logger.info("Generated {} presigned URLs for program images", urlMap.size());
        return urlMap;
    }

    @Override
    public String uploadCoverImage(Long programId, MultipartFile coverImage) {
        if (coverImage == null || coverImage.isEmpty()) {
            logger.debug("No cover image provided for program {}", programId);
            return null;
        }

        // Validate image
        ImageValidationService.ValidationResult validationResult = imageValidationService.validate(coverImage);
        if (!validationResult.valid()) {
            logger.error("Cover image validation failed for program {}: {}", programId,
                    validationResult.errorMessage());
            throw new IllegalArgumentException("Invalid cover image: " + validationResult.errorMessage());
        }

        // Generate unique key
        String key = generateImageKey(programId, "cover", coverImage.getOriginalFilename());

        // Get bucket name
        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();

        // Upload to R2
        boolean success = r2FileManagerClient.uploadFile(bucketName, key, coverImage);

        if (!success) {
            logger.error("Failed to upload cover image to R2 for program {}", programId);
            throw new RuntimeException("Failed to upload cover image to R2");
        }

        logger.info("Successfully uploaded cover image for program {}: {}", programId, key);
        return key;
    }

    @Override
    public List<String> uploadGalleryImages(Long programId, List<MultipartFile> galleryImages) {
        if (galleryImages == null || galleryImages.isEmpty()) {
            logger.debug("No gallery images provided for program {}", programId);
            return List.of();
        }

        List<String> uploadedKeys = new ArrayList<>();
        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();

        for (int i = 0; i < galleryImages.size(); i++) {
            MultipartFile image = galleryImages.get(i);

            if (image.isEmpty()) {
                logger.warn("Skipping empty gallery image at index {} for program {}", i, programId);
                continue;
            }

            // Validate image
            ImageValidationService.ValidationResult validationResult = imageValidationService.validate(image);
            if (!validationResult.valid()) {
                logger.error("Gallery image validation failed at index {} for program {}: {}",
                        i, programId, validationResult.errorMessage());
                throw new IllegalArgumentException(
                        String.format("Invalid gallery image at index %d: %s", i, validationResult.errorMessage()));
            }

            // Generate unique key
            String key = generateImageKey(programId, "gallery", image.getOriginalFilename());

            // Upload to R2
            boolean success = r2FileManagerClient.uploadFile(bucketName, key, image);

            if (!success) {
                logger.error("Failed to upload gallery image at index {} to R2 for program {}", i, programId);
                throw new RuntimeException(
                        String.format("Failed to upload gallery image at index %d to R2", i));
            }

            uploadedKeys.add(key);
            logger.info("Successfully uploaded gallery image {} for program {}: {}", i, programId, key);
        }

        logger.info("Successfully uploaded {} gallery images for program {}", uploadedKeys.size(), programId);
        return uploadedKeys;
    }

    @Override
    public boolean deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            logger.debug("No image key provided for deletion");
            return true;
        }

        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();
        boolean success = r2FileManagerClient.deleteFile(bucketName, imageKey);

        if (success) {
            logger.info("Successfully deleted image from R2: {}", imageKey);
        } else {
            logger.error("Failed to delete image from R2: {}", imageKey);
        }

        return success;
    }

    /**
     * Generates a unique R2 object key for an image.
     * Format: programs/{programId}/{type}/{uuid}.{extension}
     */
    private String generateImageKey(Long programId, String type, String filename) {
        String extension = extractFileExtension(filename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("programs/%d/%s/%s.%s", programId, type, uniqueId, extension);
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
