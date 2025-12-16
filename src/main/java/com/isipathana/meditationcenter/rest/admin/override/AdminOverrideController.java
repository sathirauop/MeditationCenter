package com.isipathana.meditationcenter.rest.admin.override;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.rest.admin.override.delete.DeleteOverrideResponse;
import com.isipathana.meditationcenter.rest.admin.override.delete.DeleteOverrideUseCase;
import com.isipathana.meditationcenter.rest.admin.override.get.GetOverridesRequest;
import com.isipathana.meditationcenter.rest.admin.override.get.GetOverridesResponse;
import com.isipathana.meditationcenter.rest.admin.override.get.GetOverridesUseCase;
import com.isipathana.meditationcenter.rest.admin.override.getbydate.GetOverrideByDateRequest;
import com.isipathana.meditationcenter.rest.admin.override.getbydate.GetOverrideByDateResponse;
import com.isipathana.meditationcenter.rest.admin.override.getbydate.GetOverrideByDateUseCase;
import com.isipathana.meditationcenter.rest.admin.override.post.PostOverrideRequest;
import com.isipathana.meditationcenter.rest.admin.override.post.PostOverrideResponse;
import com.isipathana.meditationcenter.rest.admin.override.post.PostOverrideUseCase;
import com.isipathana.meditationcenter.rest.admin.override.put.PutOverrideRequest;
import com.isipathana.meditationcenter.rest.admin.override.put.PutOverrideResponse;
import com.isipathana.meditationcenter.rest.admin.override.put.PutOverrideUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for schedule override management endpoints.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Override.BASE)
@RequiredArgsConstructor
public class AdminOverrideController {

    private final PostOverrideUseCase postOverrideUseCase;
    private final GetOverridesUseCase getOverridesUseCase;
    private final GetOverrideByDateUseCase getOverrideByDateUseCase;
    private final PutOverrideUseCase putOverrideUseCase;
    private final DeleteOverrideUseCase deleteOverrideUseCase;

    /**
     * Create a new schedule override for a specific date.
     * Requires ADMIN role and CREATE_TEMPLATE permission.
     *
     * @param request the override details with activities
     * @return the created override with activities
     */
    @PostMapping(EndPoints.Admin.Override.CREATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_TEMPLATE')")
    public ResponseEntity<PostOverrideResponse> createOverride(@Valid @RequestBody PostOverrideRequest request) {
        PostOverrideResponse response = postOverrideUseCase.handle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all schedule overrides with pagination and optional date filtering.
     * Requires ADMIN role and VIEW_TEMPLATES permission.
     *
     * @param page page number (starts from 1)
     * @param limit items per page
     * @param fromDate optional start date filter
     * @param toDate optional end date filter
     * @return paginated list of overrides
     */
    @GetMapping(EndPoints.Admin.Override.GET_ALL)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
    public ResponseEntity<OffsetSearchResponse<GetOverridesResponse>> getOverrides(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        GetOverridesRequest request = new GetOverridesRequest(page, limit, fromDate, toDate);
        OffsetSearchResponse<GetOverridesResponse> response = getOverridesUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get override for a specific date.
     * Requires ADMIN role and VIEW_TEMPLATES permission.
     *
     * @param date the date to get override for (format: yyyy-MM-dd)
     * @return the override with full activity details
     */
    @GetMapping(EndPoints.Admin.Override.GET_BY_DATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
    public ResponseEntity<GetOverrideByDateResponse> getOverrideByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GetOverrideByDateRequest request = new GetOverrideByDateRequest(date);
        GetOverrideByDateResponse response = getOverrideByDateUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing override (replaces date, reason, and all activities).
     * Requires ADMIN role and UPDATE_TEMPLATE permission.
     *
     * @param id the override ID
     * @param request the updated override details
     * @return the updated override with activities
     */
    @PutMapping(EndPoints.Admin.Override.UPDATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_TEMPLATE')")
    public ResponseEntity<PutOverrideResponse> updateOverride(
            @PathVariable Long id,
            @Valid @RequestBody PutOverrideRequest request) {
        // Create new request with ID from path variable
        PutOverrideRequest requestWithId = new PutOverrideRequest(
                id,
                request.overrideDate(),
                request.activities()
        );
        PutOverrideResponse response = putOverrideUseCase.handle(requestWithId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a schedule override by ID.
     * Requires ADMIN role and DELETE_TEMPLATE permission.
     *
     * @param id the override ID
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Override.DELETE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_TEMPLATE')")
    public ResponseEntity<DeleteOverrideResponse> deleteOverride(@PathVariable Long id) {
        DeleteOverrideResponse response = deleteOverrideUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a schedule override by date.
     * Requires ADMIN role and DELETE_TEMPLATE permission.
     *
     * @param date the override date (format: yyyy-MM-dd)
     * @return deletion confirmation
     */
    @DeleteMapping(EndPoints.Admin.Override.DELETE_BY_DATE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_TEMPLATE')")
    public ResponseEntity<DeleteOverrideResponse> deleteOverrideByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DeleteOverrideResponse response = deleteOverrideUseCase.executeByDate(date);
        return ResponseEntity.ok(response);
    }
}
