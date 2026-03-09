package com.isipathana.meditationcenter.records.gallery;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Gallery group domain record.
 * Represents a named collection/album of photos.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GalleryGroup(
        Long groupId,

        // Content (bilingual)
        String name,
        String nameSi,

        // Display
        Integer sortOrder,
        Boolean active,

        // Timestamps
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
