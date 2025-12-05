package com.isipathana.meditationcenter.rest.admin.template.activities;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.admin.template.activities.bulk.BulkUpdateTemplateActivitiesRequest;
import com.isipathana.meditationcenter.rest.admin.template.activities.bulk.BulkUpdateTemplateActivitiesResponse;
import com.isipathana.meditationcenter.rest.admin.template.activities.bulk.BulkUpdateTemplateActivitiesUseCase;
import com.isipathana.meditationcenter.rest.admin.template.activities.delete.DeleteTemplateActivityResponse;
import com.isipathana.meditationcenter.rest.admin.template.activities.delete.DeleteTemplateActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.template.activities.post.PostTemplateActivityRequest;
import com.isipathana.meditationcenter.rest.admin.template.activities.post.PostTemplateActivityResponse;
import com.isipathana.meditationcenter.rest.admin.template.activities.post.PostTemplateActivityUseCase;
import com.isipathana.meditationcenter.rest.admin.template.activities.put.PutTemplateActivityRequest;
import com.isipathana.meditationcenter.rest.admin.template.activities.put.PutTemplateActivityResponse;
import com.isipathana.meditationcenter.rest.admin.template.activities.put.PutTemplateActivityUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for template activity management endpoints.
 * Handles operations for individual activities within templates.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Template.BASE)
@RequiredArgsConstructor
public class AdminTemplateActivitiesController {

    private final PostTemplateActivityUseCase postTemplateActivityUseCase;
    private final PutTemplateActivityUseCase putTemplateActivityUseCase;
    private final DeleteTemplateActivityUseCase deleteTemplateActivityUseCase;
    private final BulkUpdateTemplateActivitiesUseCase bulkUpdateTemplateActivitiesUseCase;

    /**
     * Add a single activity to a template.
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param id the template ID
     * @param request the activity details
     * @return the created template activity
     */
    @PostMapping(EndPoints.Admin.Template.Activities.ADD)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<PostTemplateActivityResponse> addActivityToTemplate(
            @PathVariable Long id,
            @Valid @RequestBody PostTemplateActivityRequest request) {
        // Create new request with template ID from path variable
        PostTemplateActivityRequest requestWithId = new PostTemplateActivityRequest(
                id,
                request.activityId(),
                request.startTime(),
                request.endTime(),
                request.notes()
        );
        PostTemplateActivityResponse response = postTemplateActivityUseCase.handle(requestWithId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a template activity's time and notes.
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param templateId the template ID
     * @param activityId the template activity ID
     * @param request the updated activity details
     * @return the updated template activity
     */
    @PutMapping(EndPoints.Admin.Template.Activities.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<PutTemplateActivityResponse> updateTemplateActivity(
            @PathVariable Long templateId,
            @PathVariable Long activityId,
            @Valid @RequestBody PutTemplateActivityRequest request) {
        // Create new request with IDs from path variables
        PutTemplateActivityRequest requestWithIds = new PutTemplateActivityRequest(
                templateId,
                activityId,
                request.startTime(),
                request.endTime(),
                request.notes()
        );
        PutTemplateActivityResponse response = putTemplateActivityUseCase.handle(requestWithIds);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove an activity from a template.
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param templateId the template ID
     * @param activityId the template activity ID
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Template.Activities.DELETE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<DeleteTemplateActivityResponse> removeActivityFromTemplate(
            @PathVariable Long templateId,
            @PathVariable Long activityId) {
        DeleteTemplateActivityResponse response = deleteTemplateActivityUseCase.execute(templateId, activityId);

        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Bulk update all activities for a template (replaces all existing activities).
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param id the template ID
     * @param request the new list of activities
     * @return the updated template with all activities
     */
    @PutMapping(EndPoints.Admin.Template.Activities.BULK_UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<BulkUpdateTemplateActivitiesResponse> bulkUpdateTemplateActivities(
            @PathVariable Long id,
            @Valid @RequestBody BulkUpdateTemplateActivitiesRequest request) {
        // Create new request with template ID from path variable
        BulkUpdateTemplateActivitiesRequest requestWithId = new BulkUpdateTemplateActivitiesRequest(
                id,
                request.activities()
        );
        BulkUpdateTemplateActivitiesResponse response = bulkUpdateTemplateActivitiesUseCase.handle(requestWithId);
        return ResponseEntity.ok(response);
    }
}
