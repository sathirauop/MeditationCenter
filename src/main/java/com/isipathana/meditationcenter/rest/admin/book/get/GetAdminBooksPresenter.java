package com.isipathana.meditationcenter.rest.admin.book.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;
import com.isipathana.meditationcenter.rest.book.get.GetBooksHttpDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming books to admin response DTOs with presigned URLs.
 * Reuses public GetBooksHttpDataAccess for presigned URL generation.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetAdminBooksPresenter implements GetAdminBooksResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    // Reuse public HTTP data access for presigned URLs
    @Autowired(required = false)
    private GetBooksHttpDataAccess httpRepository;

    @Override
    public OffsetSearchResponse<GetAdminBooksResponse> build(
            Stream<Book> books,
            long currentOffset,
            long maxOffset) {

        List<GetAdminBooksResponse> responseList = books
                .map(this::mapBookToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    private GetAdminBooksResponse mapBookToResponse(Book book) {
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

        return GetAdminBooksResponse.builder()
                .bookId(book.bookId())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pdfUrl(pdfUrl)
                .coverImageUrl(coverImageUrl)
                .isActive(book.isActive())
                .build();
    }
}
