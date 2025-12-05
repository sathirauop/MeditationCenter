package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repository implementation for deleting event images from R2 storage.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 * <p>
 * Uses batch deletion via folder removal for efficiency - deletes all images
 * under events/{eventId}/ in a single operation.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class DeleteEventHttpRepository implements DeleteEventHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(DeleteEventHttpRepository.class);

    private final R2FileManagerClient r2FileManagerClient;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public boolean deleteCoverImage(String coverImageKey) {
        if (coverImageKey == null || coverImageKey.isEmpty()) {
            logger.debug("No cover image key provided, skipping deletion");
            return true;
        }

        String bucketName = r2ClientProperties.buckets().get("events").bucketName();

        boolean success = r2FileManagerClient.deleteFile(bucketName, coverImageKey);

        if (success) {
            logger.info("Successfully deleted cover image from R2: {}", coverImageKey);
        } else {
            logger.error("Failed to delete cover image from R2: {}", coverImageKey);
        }

        return success;
    }

    @Override
    public boolean deleteGalleryImages(Set<String> galleryImageKeys) {
        if (galleryImageKeys == null || galleryImageKeys.isEmpty()) {
            logger.debug("No gallery image keys provided, skipping deletion");
            return true;
        }

        String bucketName = r2ClientProperties.buckets().get("events").bucketName();
        boolean allSuccessful = true;

        for (String imageKey : galleryImageKeys) {
            boolean success = r2FileManagerClient.deleteFile(bucketName, imageKey);

            if (success) {
                logger.info("Successfully deleted gallery image from R2: {}", imageKey);
            } else {
                logger.error("Failed to delete gallery image from R2: {}", imageKey);
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }

    @Override
    public boolean deleteEventFolder(Long eventId) {
        if (eventId == null) {
            logger.error("Event ID is null, cannot delete event folder");
            return false;
        }

        // Event folder structure: events/{eventId}/
        String folderKey = String.format("events/%d/", eventId);
        String bucketName = r2ClientProperties.buckets().get("events").bucketName();

        logger.info("Deleting entire event folder from R2: bucket={}, folder={}", bucketName, folderKey);

        boolean success = r2FileManagerClient.deleteFolder(bucketName, folderKey);

        if (success) {
            logger.info("Successfully deleted event folder from R2: {}", folderKey);
        } else {
            logger.error("Failed to delete event folder from R2: {}", folderKey);
        }

        return success;
    }
}
