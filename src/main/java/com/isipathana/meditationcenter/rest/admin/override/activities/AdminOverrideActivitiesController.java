package com.isipathana.meditationcenter.rest.admin.override.activities;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.admin.override.activities.delete.DeleteOverrideActivityResponse;
import com.isipathana.meditationcenter.rest.admin.override.activities.delete.DeleteOverrideActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.override.activities.post.PostOverrideActivityRequest;
import com.isipathana.meditationcenter.rest.admin.override.activities.post.PostOverrideActivityResponse;
import com.isipathana.meditationcenter.rest.admin.override.activities.post.PostOverrideActivityUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for override activity management endpoints.
 * Handles operations for individual activities within overrides.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Override.BASE)
@RequiredArgsConstructor
public class AdminOverrideActivitiesController {

    private final PostOverrideActivityUseCase postOverrideActivityUseCase;
    private final DeleteOverrideActivityUseCase deleteOverrideActivityUseCase;

    /**
     * Add a single activity to an override.
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param id the override ID
     * @param request the activity details
     * @return the created override activity
     */
    @PostMapping(EndPoints.Admin.Override.Activities.ADD)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<PostOverrideActivityResponse> addActivityToOverride(
            @PathVariable Long id,
            @Valid @RequestBody PostOverrideActivityRequest request) {
        // Create new request with override ID from path variable
        PostOverrideActivityRequest requestWithId = new PostOverrideActivityRequest(
                id,
                request.activityId(),
                request.startTime(),
                request.endTime(),
                request.notes()
        );
        PostOverrideActivityResponse response = postOverrideActivityUseCase.handle(requestWithId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Remove an activity from an override.
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param overrideId the override ID
     * @param activityId the override activity ID
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Override.Activities.DELETE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<DeleteOverrideActivityResponse> removeActivityFromOverride(
            @PathVariable Long overrideId,
            @PathVariable Long activityId) {
        DeleteOverrideActivityResponse response = deleteOverrideActivityUseCase.execute(overrideId, activityId);

        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
