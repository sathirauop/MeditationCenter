package com.isipathana.meditationcenter.rest.admin.template;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.template.get.GetTemplatesRequest;
import com.isipathana.meditationcenter.rest.admin.template.get.GetTemplatesResponse;
import com.isipathana.meditationcenter.rest.admin.template.get.GetTemplatesUseCase;
import com.isipathana.meditationcenter.rest.admin.template.getActive.GetActiveTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.getActive.GetActiveTemplateUseCase;
import com.isipathana.meditationcenter.rest.admin.template.getSingle.GetSingleTemplateRequest;
import com.isipathana.meditationcenter.rest.admin.template.getSingle.GetSingleTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.getSingle.GetSingleTemplateUseCase;
import com.isipathana.meditationcenter.rest.admin.template.post.PostTemplateRequest;
import com.isipathana.meditationcenter.rest.admin.template.post.PostTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.post.PostTemplateUseCase;
import com.isipathana.meditationcenter.rest.admin.template.put.PutTemplateRequest;
import com.isipathana.meditationcenter.rest.admin.template.put.PutTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.put.PutTemplateUseCase;
import com.isipathana.meditationcenter.rest.admin.template.activate.ActivateTemplateRequest;
import com.isipathana.meditationcenter.rest.admin.template.activate.ActivateTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.activate.ActivateTemplateUseCase;
import com.isipathana.meditationcenter.rest.admin.template.delete.DeleteTemplateResponse;
import com.isipathana.meditationcenter.rest.admin.template.delete.DeleteTemplateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin template management endpoints.
 * Handles CRUD operations for schedule templates.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Template.BASE)
@RequiredArgsConstructor
public class AdminTemplateController {

    private final PostTemplateUseCase postTemplateUseCase;
    private final GetTemplatesUseCase getTemplatesUseCase;
    private final GetActiveTemplateUseCase getActiveTemplateUseCase;
    private final GetSingleTemplateUseCase getSingleTemplateUseCase;
    private final PutTemplateUseCase putTemplateUseCase;
    private final ActivateTemplateUseCase activateTemplateUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;

    /**
     * Create a new schedule template with activities.
     * Requires ADMIN role and CREATE_TEMPLATE permission.
     *
     * @param request the template details with activities
     * @return the created template
     */
    @PostMapping(EndPoints.Admin.Template.CREATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_TEMPLATE')")
    public ResponseEntity<PostTemplateResponse> createTemplate(
            @Valid @RequestBody PostTemplateRequest request) {
        PostTemplateResponse response = postTemplateUseCase.handle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all templates with pagination.
     * Requires ADMIN role and VIEW_TEMPLATES permission.
     *
     * @param limit the maximum number of results per page (default: 20, max: 100)
     * @param offset the page offset (default: 0)
     * @return paginated list of templates
     */
    @GetMapping(EndPoints.Admin.Template.GET_ALL)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
    public ResponseEntity<OffsetSearchResponse<GetTemplatesResponse>> getTemplates(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        GetTemplatesRequest request = new GetTemplatesRequest(limit, offset);
        OffsetSearchResponse<GetTemplatesResponse> response = getTemplatesUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the currently active template with all activities.
     * Requires ADMIN role and VIEW_TEMPLATES permission.
     *
     * @return the active template with activities
     */
    @GetMapping(EndPoints.Admin.Template.GET_ACTIVE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
    public ResponseEntity<GetActiveTemplateResponse> getActiveTemplate() {
        GetActiveTemplateResponse response = getActiveTemplateUseCase.handle();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a single template by ID with full activity details.
     * Requires ADMIN role and VIEW_TEMPLATES permission.
     *
     * @param id the template ID
     * @return the template with full activity details
     */
    @GetMapping(EndPoints.Admin.Template.GET_BY_ID)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
    public ResponseEntity<GetSingleTemplateResponse> getTemplateById(@PathVariable Long id) {
        GetSingleTemplateRequest request = new GetSingleTemplateRequest(id);
        GetSingleTemplateResponse response = getSingleTemplateUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a template (full replacement including activities).
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param id the template ID
     * @param request the updated template details with activities
     * @return the updated template
     */
    @PutMapping(EndPoints.Admin.Template.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<PutTemplateResponse> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody PutTemplateRequest request) {
        // Create new request with ID from path variable
        PutTemplateRequest requestWithId = new PutTemplateRequest(
                id,
                request.name(),
                request.description(),
                request.activities()
        );
        PutTemplateResponse response = putTemplateUseCase.handle(requestWithId);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate a template (deactivates all other templates).
     * Requires ADMIN role and ACTIVATE_TEMPLATE permission.
     * Only ONE template can be active at a time.
     *
     * @param id the template ID to activate
     * @return activation confirmation with previous active template info
     */
    @PatchMapping(EndPoints.Admin.Template.ACTIVATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('ACTIVATE_TEMPLATE')")
    public ResponseEntity<ActivateTemplateResponse> activateTemplate(@PathVariable Long id) {
        ActivateTemplateRequest request = new ActivateTemplateRequest(id);
        ActivateTemplateResponse response = activateTemplateUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a template by ID.
     * Requires ADMIN role and DELETE_TEMPLATE permission.
     * Note: Associated activities are automatically deleted via CASCADE.
     *
     * @param id the template ID
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Template.DELETE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_TEMPLATE')")
    public ResponseEntity<DeleteTemplateResponse> deleteTemplate(@PathVariable Long id) {
        DeleteTemplateResponse response = deleteTemplateUseCase.execute(id);

        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
