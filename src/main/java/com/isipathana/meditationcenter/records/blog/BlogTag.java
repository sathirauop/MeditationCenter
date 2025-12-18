package com.isipathana.meditationcenter.records.blog;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Blog tag domain record.
 * Represents a tag for categorizing blog posts with bilingual support.
 *
 * @author Sathira Basnayake
 */
@Builder
public record BlogTag(
        Long tagId,
        String name,
        String nameSi,
        String slug,
        LocalDateTime createdAt
) {
}
