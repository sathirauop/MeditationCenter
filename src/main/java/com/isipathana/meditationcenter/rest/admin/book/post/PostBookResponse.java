package com.isipathana.meditationcenter.rest.admin.book.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for created book.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PostBookResponse(
        @JsonProperty("book_id") Long bookId,
        String title,
        String author,
        String description,
        @JsonProperty("pdf_file_key") String pdfFileKey,
        @JsonProperty("cover_image_key") String coverImageKey,
        @JsonProperty("pdf_url") String pdfUrl,
        @JsonProperty("cover_image_url") String coverImageUrl,
        @JsonProperty("is_active") Boolean isActive,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) implements ApiResponse {}
