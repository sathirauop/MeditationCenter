package com.isipathana.meditationcenter.rest.events;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.events.admin.get.GetAdminEventsRequest;
import com.isipathana.meditationcenter.rest.events.admin.get.GetAdminEventsResponse;
import com.isipathana.meditationcenter.rest.events.admin.get.GetAdminEventsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for admin events endpoints.
 * Handles HTTP requests for admin event operations.
 * All endpoints require ADMIN role.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventsController {

    private final GetAdminEventsUseCase getAdminEventsUseCase;

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
