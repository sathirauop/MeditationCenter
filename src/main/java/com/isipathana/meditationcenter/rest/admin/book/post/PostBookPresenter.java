package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.records.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming created book to response DTO with presigned URLs.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostBookPresenter implements PostBookResponseBuilder {

    @Autowired(required = false)
    private R2PresignerClient r2PresignerClient;

    @Autowired(required = false)
    private R2ClientProperties r2ClientProperties;

    @Override
    public PostBookResponse build(Book book) {
        String pdfUrl = null;
        String coverImageUrl = null;

        // Generate presigned URLs if R2 is enabled
        if (r2PresignerClient != null && r2ClientProperties != null) {
            String bucketName = r2ClientProperties.buckets().get("books").bucketName();
            var expiry = r2ClientProperties.buckets().get("books").presignedUrlExpiry();

            if (book.pdfFileKey() != null) {
                pdfUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, book.pdfFileKey(), expiry);
            }

            if (book.coverImageKey() != null) {
                coverImageUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, book.coverImageKey(), expiry);
            }
        }

        return PostBookResponse.builder()
                .bookId(book.bookId())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pdfFileKey(book.pdfFileKey())
                .coverImageKey(book.coverImageKey())
                .pdfUrl(pdfUrl)
                .coverImageUrl(coverImageUrl)
                .isActive(book.isActive())
                .createdAt(book.createdAt())
                .updatedAt(book.updatedAt())
                .build();
    }
}
