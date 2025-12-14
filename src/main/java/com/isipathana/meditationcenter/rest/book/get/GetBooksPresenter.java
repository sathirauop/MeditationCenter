package com.isipathana.meditationcenter.rest.book.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming books to response DTOs with presigned URLs.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetBooksPresenter implements GetBooksResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    @Autowired(required = false)
    private GetBooksHttpDataAccess httpRepository;

    @Override
    public OffsetSearchResponse<GetBooksResponse> build(
            Stream<Book> books,
            long currentOffset,
            long maxOffset) {

        List<GetBooksResponse> responseList = books
                .map(this::mapBookToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    private GetBooksResponse mapBookToResponse(Book book) {
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

        return GetBooksResponse.builder()
                .bookId(book.bookId())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pdfUrl(pdfUrl)
                .coverImageUrl(coverImageUrl)
                .build();
    }
}
