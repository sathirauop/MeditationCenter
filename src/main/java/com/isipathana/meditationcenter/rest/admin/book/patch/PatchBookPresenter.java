package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming updated book to response DTO with presigned URLs.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class PatchBookPresenter implements PatchBookResponseBuilder {

    // Optional - only injected if R2 is enabled
    @Autowired(required = false)
    private PatchBookHttpDataAccess httpRepository;

    @Override
    public PatchBookResponse build(Book book) {
        String pdfUrl = null;
        String coverImageUrl = null;

        // Generate presigned URLs if R2 is enabled
        if (httpRepository != null) {
            if (book.pdfFileKey() != null && !book.pdfFileKey().isEmpty()) {
                pdfUrl = httpRepository.generatePresignedUrl(book.pdfFileKey());
            }

            if (book.coverImageKey() != null && !book.coverImageKey().isEmpty()) {
                coverImageUrl = httpRepository.generatePresignedUrl(book.coverImageKey());
            }
        }

        return PatchBookResponse.builder()
                .bookId(book.bookId())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pdfUrl(pdfUrl)
                .coverImageUrl(coverImageUrl)
                .isActive(book.isActive())
                .message("Book updated successfully")
                .build();
    }
}
