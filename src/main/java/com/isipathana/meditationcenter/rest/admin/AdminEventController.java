package com.isipathana.meditationcenter.rest.admin;

import com.isipathana.meditationcenter.rest.admin.event.PostEventRequest;
import com.isipathana.meditationcenter.rest.admin.event.PostEventResponse;
import com.isipathana.meditationcenter.rest.admin.event.PostEventUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final PostEventUseCase postEventUseCase;

    /**
     * Create a new event.
     * <p>
     * POST /api/admin/event
     * <p>
     * Requires: ADMIN role with CREATE_EVENT permission
     *
     * @param request Event creation request
     * @return 201 Created with event details
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_EVENT')")
    public ResponseEntity<PostEventResponse> createEvent(@Valid @RequestBody PostEventRequest request) {
        PostEventResponse response = postEventUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
