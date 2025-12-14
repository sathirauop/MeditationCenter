package com.isipathana.meditationcenter.rest.book.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

/**
 * Response DTO for book retrieval with presigned URLs.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetBooksResponse(
        @JsonProperty("book_id")
        Long bookId,

        String title,

        String author,

        String description,

        @JsonProperty("pdf_url")
        String pdfUrl,

        @JsonProperty("cover_image_url")
        String coverImageUrl
) implements ApiResponse {
}
