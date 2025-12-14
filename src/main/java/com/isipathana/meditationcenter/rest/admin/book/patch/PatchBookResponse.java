package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;

/**
 * Response DTO for book update operation.
 * Returns the updated book details with presigned URLs.
 *
 * @author Sathira Basnayake
 */
public record PatchBookResponse(
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
        Boolean isActive,

        String message
) implements ApiResponse {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long bookId;
        private String title;
        private String author;
        private String description;
        private String pdfUrl;
        private String coverImageUrl;
        private Boolean isActive;
        private String message;

        public Builder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder pdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public Builder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public PatchBookResponse build() {
            return new PatchBookResponse(
                    bookId,
                    title,
                    author,
                    description,
                    pdfUrl,
                    coverImageUrl,
                    isActive,
                    message
            );
        }
    }
}
