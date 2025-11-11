package com.isipathana.meditationcenter.rest.event;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.event.get.GetEventsRequest;
import com.isipathana.meditationcenter.rest.event.get.GetEventsResponse;
import com.isipathana.meditationcenter.rest.event.get.GetEventsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin REST controller for event endpoints.
 * <p>
 * This endpoint is public (no authentication required for viewing events).
 * Configured in SecurityConfig.java.
 * <p>
 * This controller only handles HTTP concerns (routing, status codes).
 * All business logic is delegated to UseCases.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final GetEventsUseCase getEventsUseCase;

    /**
     * Get active events with pagination.
     * <p>
     * GET /api/event?limit=20&offset=0
     * <p>
     * Returns active events ordered by event date and start time.
     * No authentication required - public endpoint.
     *
     * @param limit  Maximum number of results (default: 20, max: 100)
     * @param offset Page offset for pagination (default: 0)
     * @return 200 OK with paginated list of active events
     */
    @GetMapping
    public ResponseEntity<OffsetSearchResponse<GetEventsResponse>> getEvents(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        GetEventsRequest request = new GetEventsRequest(limit, offset);
        OffsetSearchResponse<GetEventsResponse> response = getEventsUseCase.handle(request);
        return ResponseEntity.ok(response);
    }
}
