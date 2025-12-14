package com.isipathana.meditationcenter.rest.admin.book.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

/**
 * Response DTO for admin book retrieval with presigned URLs and active status.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminBooksResponse(
        @JsonProperty("book_id")
        Long bookId,

        String title,

        String author,

        String description,

        @JsonProperty("pdf_url")
        String pdfUrl,

        @JsonProperty("cover_image_url")
        String coverImageUrl,

        @JsonProperty("is_active")
        Boolean isActive
) implements ApiResponse {
}
