package com.isipathana.meditationcenter.records.gallery;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Gallery photo domain record.
 * Represents a single photo within a gallery group.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GalleryPhoto(
        Long photoId,
        Long groupId,

        // Image (R2 storage key)
        String imageKey,

        // Content (bilingual)
        String caption,
        String captionSi,

        // Display
        Integer sortOrder,

        // Timestamps
        LocalDateTime createdAt) {
}
