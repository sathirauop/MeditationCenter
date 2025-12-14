package com.isipathana.meditationcenter.rest.admin.book.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving paginated list of all books (admin view).
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class GetAdminBooksUseCase implements UseCase<GetAdminBooksRequest, OffsetSearchResponse<GetAdminBooksResponse>> {

    private final GetAdminBooksDataAccess repository;
    private final GetAdminBooksResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetAdminBooksResponse> handle(GetAdminBooksRequest request) {
        // Calculate actual offset from page-based offset
        int actualOffset = request.offset() * request.limit();

        // Fetch all books (active and inactive) with pagination
        List<Book> books = repository.findAllBooks(actualOffset, request.limit());

        // Get total count for pagination metadata
        long totalCount = repository.getAllBookCount();

        // Use presenter to build response
        return responseBuilder.build(
                books.stream(),
                actualOffset,
                totalCount
        );
    }
}
