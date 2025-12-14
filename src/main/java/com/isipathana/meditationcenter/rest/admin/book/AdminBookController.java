package com.isipathana.meditationcenter.rest.admin.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.book.get.GetAdminBooksRequest;
import com.isipathana.meditationcenter.rest.admin.book.get.GetAdminBooksResponse;
import com.isipathana.meditationcenter.rest.admin.book.get.GetAdminBooksUseCase;
import com.isipathana.meditationcenter.rest.admin.book.patch.PatchBookRequest;
import com.isipathana.meditationcenter.rest.admin.book.patch.PatchBookResponse;
import com.isipathana.meditationcenter.rest.admin.book.patch.PatchBookUseCase;
import com.isipathana.meditationcenter.rest.admin.book.post.PostBookRequest;
import com.isipathana.meditationcenter.rest.admin.book.post.PostBookResponse;
import com.isipathana.meditationcenter.rest.admin.book.post.PostBookUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for admin book management endpoints.
 * All endpoints require ADMIN role.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Book.BASE)
@RequiredArgsConstructor
public class AdminBookController {

    private final PostBookUseCase postBookUseCase;
    private final GetAdminBooksUseCase getAdminBooksUseCase;
    private final PatchBookUseCase patchBookUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Create a new book with PDF and optional cover image.
     * <p>
     * POST /api/admin/book
     * <p>
     * Requires: ADMIN role with CREATE_BOOK permission
     * <p>
     * Accepts multipart/form-data with:
     * - book: JSON string of PostBookRequest (title, author, description)
     * - pdfFile (required): PDF file (max 50MB)
     * - coverImage (optional): Cover image file (JPEG, PNG, GIF, WebP, max 5MB)
     *
     * @param bookJson   Book creation request as JSON string
     * @param pdfFile    PDF file (required)
     * @param coverImage Optional cover image file
     * @return 201 Created with book details and presigned URLs
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_BOOK')")
    public ResponseEntity<PostBookResponse> createBook(
            @RequestPart("book") String bookJson,
            @RequestPart("pdfFile") MultipartFile pdfFile,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) throws Exception {
        // Parse JSON request
        PostBookRequest request = objectMapper.readValue(bookJson, PostBookRequest.class);

        // Execute use case with files
        PostBookResponse response = postBookUseCase.execute(request, pdfFile, coverImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Create a new book without files (JSON-only endpoint for testing).
     * <p>
     * POST /api/admin/book/json
     * <p>
     * Requires: ADMIN role with CREATE_BOOK permission
     * <p>
     * NOTE: This endpoint is primarily for testing when R2 is disabled.
     * In production, use the multipart endpoint above.
     *
     * @param request Book creation request
     * @return 201 Created with book details
     */
    @PostMapping(value = EndPoints.Admin.Book.CREATE_JSON, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_BOOK')")
    public ResponseEntity<PostBookResponse> createBookJson(@Valid @RequestBody PostBookRequest request) {
        PostBookResponse response = postBookUseCase.execute(request, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get paginated list of all books (active and inactive).
     * <p>
     * GET /api/admin/book?limit=20&offset=0
     * <p>
     * Requires: ADMIN role with VIEW_BOOKS permission
     * <p>
     * Returns all books including inactive ones with presigned URLs.
     *
     * @param limit  Maximum number of results (default: 20, max: 100)
     * @param offset Page offset for pagination (default: 0)
     * @return Paginated response with book list and presigned URLs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_BOOKS')")
    public ResponseEntity<OffsetSearchResponse<GetAdminBooksResponse>> getAdminBooks(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        GetAdminBooksRequest request = new GetAdminBooksRequest(limit, offset);
        OffsetSearchResponse<GetAdminBooksResponse> response = getAdminBooksUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update book information (partial update).
     * <p>
     * PATCH /api/admin/book/{bookId}
     * <p>
     * Requires: ADMIN role with UPDATE_BOOK permission
     * <p>
     * All fields in the request are optional - only provided fields will be updated.
     * At least one field must be provided.
     *
     * @param bookId  ID of the book to update
     * @param request Request with fields to update (all optional)
     * @return 200 OK with updated book details and presigned URLs
     */
    @PatchMapping(EndPoints.Admin.Book.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_BOOK')")
    public ResponseEntity<PatchBookResponse> updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody PatchBookRequest request
    ) {
        PatchBookResponse response = patchBookUseCase.execute(bookId, request);
        return ResponseEntity.ok(response);
    }
}
