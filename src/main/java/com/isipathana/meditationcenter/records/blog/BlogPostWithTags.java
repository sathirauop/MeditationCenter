package com.isipathana.meditationcenter.records.blog;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Blog post with tag names included (for public API responses).
 * Extends BlogPost with denormalized tag names for efficient API responses.
 *
 * @author Sathira Basnayake
 */
@Builder
public record BlogPostWithTags(
        Long postId,
        String title,
        String excerpt,
        String content,
        String titleSi,
        String excerptSi,
        String contentSi,
        String slug,
        Long authorId,
        String authorName,
        String coverImageKey,
        Set<String> imageKeys,
        BlogPostStatus status,
        LocalDateTime publishedAt,
        String metaTitle,
        String metaDescription,
        Long viewCount,
        Set<String> tagNames,  // Denormalized tag names (not IDs)
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
