package com.isipathana.meditationcenter.records.blog;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Blog post domain record.
 * Represents a blog post with bilingual content support (English/Sinhala).
 *
 * @author Sathira Basnayake
 */
@Builder
public record BlogPost(
        Long postId,

        // English content
        String title,
        String excerpt,
        String content,

        // Sinhala content
        String titleSi,
        String excerptSi,
        String contentSi,

        // Metadata
        String slug,
        Long authorId,
        String authorName,  // Denormalized for display (from users table join)

        // Images (R2 storage keys)
        String coverImageKey,
        Set<String> imageKeys,

        // Publishing
        BlogPostStatus status,
        LocalDateTime publishedAt,

        // SEO
        String metaTitle,
        String metaDescription,

        // Analytics
        Long viewCount,

        // Tags
        Set<Long> tagIds,  // Tag IDs for associations

        // Optimistic locking
        Long version,

        // Timestamps
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt  // Soft delete
) {
}
