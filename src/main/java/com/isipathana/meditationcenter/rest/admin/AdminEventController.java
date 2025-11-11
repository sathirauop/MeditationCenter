package com.isipathana.meditationcenter.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.event.get.GetAdminEventsRequest;
import com.isipathana.meditationcenter.rest.admin.event.get.GetAdminEventsResponse;
import com.isipathana.meditationcenter.rest.admin.event.get.GetAdminEventsUseCase;
import com.isipathana.meditationcenter.rest.admin.event.post.PostEventRequest;
import com.isipathana.meditationcenter.rest.admin.event.post.PostEventResponse;
import com.isipathana.meditationcenter.rest.admin.event.post.PostEventUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for admin event management endpoints.
 * All endpoints require ADMIN role.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping("/api/admin/event")
@RequiredArgsConstructor
public class AdminEventController {

    private final GetAdminEventsUseCase getAdminEventsUseCase;
    private final PostEventUseCase postEventUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Create a new event with optional image uploads.
     * <p>
     * POST /api/admin/event
     * <p>
     * Requires: ADMIN role with CREATE_EVENT permission
     * <p>
     * Accepts multipart/form-data with:
     * - event: JSON string of PostEventRequest
     * - coverImage (optional): Cover image file (JPEG, PNG, GIF, WebP, max 5MB)
     * - galleryImages (optional): Multiple gallery image files (JPEG, PNG, GIF, WebP, max 5MB each)
     *
     * @param eventJson     Event creation request as JSON string
     * @param coverImage    Optional cover image file
     * @param galleryImages Optional gallery image files
     * @return 201 Created with event details
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_EVENT')")
    public ResponseEntity<PostEventResponse> createEvent(
            @RequestPart("event") String eventJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages
    ) throws Exception {
        // Parse JSON request
        PostEventRequest request = objectMapper.readValue(eventJson, PostEventRequest.class);

        // Execute use case with images
        PostEventResponse response = postEventUseCase.execute(request, coverImage, galleryImages);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Create a new event without images (JSON-only endpoint for backward compatibility).
     * <p>
     * POST /api/admin/event/json
     * <p>
     * Requires: ADMIN role with CREATE_EVENT permission
     *
     * @param request Event creation request
     * @return 201 Created with event details
     */
    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_EVENT')")
    public ResponseEntity<PostEventResponse> createEventJson(@Valid @RequestBody PostEventRequest request) {
        PostEventResponse response = postEventUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/admin/events
     * Retrieve paginated list of events for admin users.
     *
     * @param limit  maximum number of events to return (default: 20, max: 100)
     * @param offset page offset for pagination (default: 0)
     * @return paginated response with event data
     */
    @GetMapping
    public ResponseEntity<OffsetSearchResponse<GetAdminEventsResponse>> getAdminEvents(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        GetAdminEventsRequest request = new GetAdminEventsRequest(limit, offset);
        OffsetSearchResponse<GetAdminEventsResponse> response = getAdminEventsUseCase.handle(request);
        return ResponseEntity.ok(response);
    }
}
