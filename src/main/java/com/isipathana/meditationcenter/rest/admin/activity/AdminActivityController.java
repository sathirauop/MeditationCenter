package com.isipathana.meditationcenter.rest.admin.activity;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.activity.get.GetActivitiesRequest;
import com.isipathana.meditationcenter.rest.admin.activity.get.GetActivitiesResponse;
import com.isipathana.meditationcenter.rest.admin.activity.get.GetActivitiesUseCase;
import com.isipathana.meditationcenter.rest.admin.activity.getSingle.GetSingleActivityRequest;
import com.isipathana.meditationcenter.rest.admin.activity.getSingle.GetSingleActivityResponse;
import com.isipathana.meditationcenter.rest.admin.activity.getSingle.GetSingleActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.activity.delete.DeleteActivityResponse;
import com.isipathana.meditationcenter.rest.admin.activity.delete.DeleteActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.activity.patch.PatchActivityRequest;
import com.isipathana.meditationcenter.rest.admin.activity.patch.PatchActivityResponse;
import com.isipathana.meditationcenter.rest.admin.activity.patch.PatchActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.activity.post.PostActivityRequest;
import com.isipathana.meditationcenter.rest.admin.activity.post.PostActivityResponse;
import com.isipathana.meditationcenter.rest.admin.activity.post.PostActivityUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin activity management endpoints.
 * Handles CRUD operations for meditation activities.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Activity.BASE)
@RequiredArgsConstructor
public class AdminActivityController {

    private final PostActivityUseCase postActivityUseCase;
    private final GetActivitiesUseCase getActivitiesUseCase;
    private final GetSingleActivityUseCase getSingleActivityUseCase;
    private final PatchActivityUseCase patchActivityUseCase;
    private final DeleteActivityUseCase deleteActivityUseCase;

    /**
     * Create a new activity.
     * Requires ADMIN role and CREATE_ACTIVITY permission.
     *
     * @param request the activity details
     * @return the created activity
     */
    @PostMapping(EndPoints.Admin.Activity.CREATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_ACTIVITY')")
    public ResponseEntity<PostActivityResponse> createActivity(
            @Valid @RequestBody PostActivityRequest request) {
        PostActivityResponse response = postActivityUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all activities with pagination.
     * Requires ADMIN role and VIEW_ACTIVITIES permission.
     *
     * @param limit the maximum number of results per page (default: 20, max: 100)
     * @param offset the page offset (default: 0)
     * @return paginated list of activities
     */
    @GetMapping(EndPoints.Admin.Activity.GET_ALL)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_ACTIVITIES')")
    public ResponseEntity<OffsetSearchResponse<GetActivitiesResponse>> getActivities(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        GetActivitiesRequest request = new GetActivitiesRequest(limit, offset);
        OffsetSearchResponse<GetActivitiesResponse> response = getActivitiesUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a single activity by ID.
     * Requires ADMIN role and VIEW_ACTIVITIES permission.
     *
     * @param id the activity ID
     * @return the activity details
     */
    @GetMapping(EndPoints.Admin.Activity.GET_BY_ID)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_ACTIVITIES')")
    public ResponseEntity<GetSingleActivityResponse> getActivityById(@PathVariable Long id) {
        GetSingleActivityRequest request = new GetSingleActivityRequest(id);
        GetSingleActivityResponse response = getSingleActivityUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an activity (partial update).
     * Requires ADMIN role and UPDATE_ACTIVITY permission.
     *
     * @param id the activity ID
     * @param request the fields to update
     * @return the updated activity
     */
    @PatchMapping(EndPoints.Admin.Activity.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_ACTIVITY')")
    public ResponseEntity<PatchActivityResponse> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody PatchActivityRequest request) {
        // Create new request with ID from path variable
        PatchActivityRequest requestWithId = new PatchActivityRequest(
                id,
                request.title(),
                request.description(),
                request.mediaUrl()
        );
        PatchActivityResponse response = patchActivityUseCase.handle(requestWithId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an activity by ID.
     * Requires ADMIN role and DELETE_ACTIVITY permission.
     *
     * @param id the activity ID
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Activity.DELETE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_ACTIVITY')")
    public ResponseEntity<DeleteActivityResponse> deleteActivity(@PathVariable Long id) {
        DeleteActivityResponse response = deleteActivityUseCase.execute(id);

        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
