package com.isipathana.meditationcenter.records.book;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Domain record representing a book resource.
 *
 * @author Sathira Basnayake
 */
@Builder
public record Book(
        Long bookId,
        String title,
        String author,
        String description,
        String pdfFileKey,
        String coverImageKey,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Factory method to create a new book for insertion.
     *
     * @param title         Book title
     * @param author        Book author
     * @param description   Book description
     * @param pdfFileKey    R2 storage key for PDF file
     * @param coverImageKey R2 storage key for cover image (optional)
     * @return New Book instance ready for database insertion
     */
    public static Book create(String title, String author, String description,
                             String pdfFileKey, String coverImageKey) {
        return Book.builder()
                .title(title)
                .author(author)
                .description(description)
                .pdfFileKey(pdfFileKey)
                .coverImageKey(coverImageKey)
                .isActive(true)
                .build();
    }
}
