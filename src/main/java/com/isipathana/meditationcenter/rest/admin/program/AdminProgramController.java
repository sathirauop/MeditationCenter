package com.isipathana.meditationcenter.rest.admin.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.admin.program.get.GetAdminProgramResponse;
import com.isipathana.meditationcenter.rest.admin.program.get.GetAdminProgramUseCase;
import com.isipathana.meditationcenter.rest.admin.program.getActive.GetAdminActiveProgramResponse;
import com.isipathana.meditationcenter.rest.admin.program.getActive.GetAdminActiveProgramUseCase;
import com.isipathana.meditationcenter.rest.admin.program.patch.PatchProgramRequest;
import com.isipathana.meditationcenter.rest.admin.program.patch.PatchProgramResponse;
import com.isipathana.meditationcenter.rest.admin.program.patch.PatchProgramUseCase;
import com.isipathana.meditationcenter.rest.admin.program.post.PostProgramRequest;
import com.isipathana.meditationcenter.rest.admin.program.post.PostProgramResponse;
import com.isipathana.meditationcenter.rest.admin.program.post.PostProgramUseCase;
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
 * REST controller for admin meditation program management endpoints.
 * All endpoints require ADMIN role.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Program.BASE)
@RequiredArgsConstructor
public class AdminProgramController {

    private final PostProgramUseCase postProgramUseCase;
    private final GetAdminProgramUseCase getAdminProgramUseCase;
    private final GetAdminActiveProgramUseCase getAdminActiveProgramUseCase;
    private final PatchProgramUseCase patchProgramUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Create a new meditation program with optional image uploads.
     * <p>
     * POST /api/admin/program
     * <p>
     * Requires: ADMIN role with CREATE_PROGRAM permission
     * <p>
     * Accepts multipart/form-data with:
     * - program: JSON string of PostProgramRequest
     * - coverImage (optional): Cover image file (JPEG, PNG, GIF, WebP, max 5MB)
     * - galleryImages (optional): Multiple gallery image files (JPEG, PNG, GIF,
     * WebP, max 5MB each)
     *
     * @param programJson   Program creation request as JSON string
     * @param coverImage    Optional cover image file
     * @param galleryImages Optional gallery image files
     * @return 201 Created with program details
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_PROGRAM')")
    public ResponseEntity<PostProgramResponse> createProgram(
            @RequestPart("program") String programJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages)
            throws Exception {
        // Parse JSON request
        PostProgramRequest request = objectMapper.readValue(programJson, PostProgramRequest.class);

        // Execute use case with images
        PostProgramResponse response = postProgramUseCase.execute(request, coverImage, galleryImages);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Create a new meditation program without images (JSON-only endpoint for
     * backward compatibility).
     * <p>
     * POST /api/admin/program/json
     * <p>
     * Requires: ADMIN role with CREATE_PROGRAM permission
     *
     * @param request Program creation request
     * @return 201 Created with program details
     */
    @PostMapping(value = EndPoints.Admin.Program.CREATE_JSON, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_PROGRAM')")
    public ResponseEntity<PostProgramResponse> createProgramJson(@Valid @RequestBody PostProgramRequest request) {
        PostProgramResponse response = postProgramUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get the currently active meditation program (admin).
     * Returns the single active program without needing to know the ID.
     * <p>
     * GET /api/admin/programs/active
     * <p>
     * Requires: ADMIN role with VIEW_PROGRAM permission
     *
     * @return 200 OK with active program details (includes presigned image URLs)
     */
    @GetMapping(EndPoints.Admin.Program.GET_ACTIVE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_PROGRAMS')")
    public ResponseEntity<GetAdminActiveProgramResponse> getActiveProgram() {
        GetAdminActiveProgramResponse response = getAdminActiveProgramUseCase.execute();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a single meditation program by ID (admin).
     * <p>
     * GET /api/admin/programs/{programId}
     * <p>
     * Requires: ADMIN role with VIEW_PROGRAM permission
     *
     * @param programId The program ID
     * @return 200 OK with program details (includes presigned image URLs)
     */
    @GetMapping(EndPoints.Admin.Program.GET_BY_ID)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_PROGRAM')")
    public ResponseEntity<GetAdminProgramResponse> getProgram(@PathVariable Long programId) {
        GetAdminProgramResponse response = getAdminProgramUseCase.execute(programId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a meditation program (partial update) with optional image uploads.
     * <p>
     * PATCH /api/admin/programs/{programId}
     * <p>
     * Requires: ADMIN role with UPDATE_PROGRAM permission
     * <p>
     * Accepts multipart/form-data with:
     * - program: JSON string of PatchProgramRequest
     * - coverImage (optional): New cover image file (JPEG, PNG, GIF, WebP, max 5MB)
     * - galleryImages (optional): New gallery image files (JPEG, PNG, GIF, WebP,
     * max 5MB each)
     *
     * @param programId     The program ID
     * @param programJson   Program update request as JSON string
     * @param coverImage    Optional new cover image file
     * @param galleryImages Optional new gallery image files
     * @return 200 OK with updated program details
     */
    @PatchMapping(value = EndPoints.Admin.Program.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_PROGRAM')")
    public ResponseEntity<PatchProgramResponse> updateProgram(
            @PathVariable Long programId,
            @RequestPart("program") String programJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "galleryImages", required = false) List<MultipartFile> galleryImages)
            throws Exception {
        // Parse JSON request
        PatchProgramRequest request = objectMapper.readValue(programJson, PatchProgramRequest.class);

        PatchProgramResponse response = patchProgramUseCase.execute(programId, request, coverImage, galleryImages);
        return ResponseEntity.ok(response);
    }
}
