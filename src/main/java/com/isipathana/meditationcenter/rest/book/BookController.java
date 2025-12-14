package com.isipathana.meditationcenter.rest.book;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.book.get.GetBooksRequest;
import com.isipathana.meditationcenter.rest.book.get.GetBooksResponse;
import com.isipathana.meditationcenter.rest.book.get.GetBooksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for public book endpoints.
 * All endpoints are publicly accessible.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Book.BASE)
@RequiredArgsConstructor
public class BookController {

    private final GetBooksUseCase getBooksUseCase;

    /**
     * Get paginated list of active books.
     * <p>
     * GET /api/books?limit=20&offset=0
     * <p>
     * No authentication required (public endpoint)
     *
     * @param limit  Maximum number of results (default: 20, max: 100)
     * @param offset Page offset for pagination (default: 0)
     * @return Paginated response with book list and presigned URLs
     */
    @GetMapping
    public ResponseEntity<OffsetSearchResponse<GetBooksResponse>> getBooks(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        GetBooksRequest request = new GetBooksRequest(limit, offset);
        OffsetSearchResponse<GetBooksResponse> response = getBooksUseCase.handle(request);
        return ResponseEntity.ok(response);
    }
}
