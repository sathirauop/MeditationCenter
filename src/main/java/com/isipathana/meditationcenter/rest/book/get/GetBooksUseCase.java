package com.isipathana.meditationcenter.rest.book.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving paginated list of active books.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class GetBooksUseCase implements UseCase<GetBooksRequest, OffsetSearchResponse<GetBooksResponse>> {

    private final GetBooksDataAccess repository;
    private final GetBooksResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetBooksResponse> handle(GetBooksRequest request) {
        // Calculate actual offset from page-based offset
        int actualOffset = request.offset() * request.limit();

        // Fetch books with pagination
        List<Book> books = repository.findActiveBooks(actualOffset, request.limit());

        // Get total count for pagination metadata
        long totalCount = repository.getActiveBookCount();

        // Use presenter to build response
        return responseBuilder.build(
                books.stream(),
                actualOffset,
                totalCount
        );
    }
}
