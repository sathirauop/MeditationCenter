package com.isipathana.meditationcenter.rest.admin.gallery;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.admin.gallery.addPhotos.AddGalleryPhotosResponse;
import com.isipathana.meditationcenter.rest.admin.gallery.addPhotos.AddGalleryPhotosUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.delete.DeleteGalleryGroupUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.deletePhoto.DeleteGalleryPhotoUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.get.GetGalleryGroupsResponse;
import com.isipathana.meditationcenter.rest.admin.gallery.get.GetGalleryGroupsUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.getSingle.GetGalleryGroupResponse;
import com.isipathana.meditationcenter.rest.admin.gallery.getSingle.GetGalleryGroupUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.patch.PatchGalleryGroupRequest;
import com.isipathana.meditationcenter.rest.admin.gallery.patch.PatchGalleryGroupUseCase;
import com.isipathana.meditationcenter.rest.admin.gallery.post.PostGalleryGroupRequest;
import com.isipathana.meditationcenter.rest.admin.gallery.post.PostGalleryGroupResponse;
import com.isipathana.meditationcenter.rest.admin.gallery.post.PostGalleryGroupUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for admin gallery management.
 * ADMIN only - requires authentication.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Admin.Gallery.BASE)
@RequiredArgsConstructor
public class AdminGalleryController {

    private final GetGalleryGroupsUseCase getGalleryGroupsUseCase;
    private final GetGalleryGroupUseCase getGalleryGroupUseCase;
    private final PostGalleryGroupUseCase postGalleryGroupUseCase;
    private final PatchGalleryGroupUseCase patchGalleryGroupUseCase;
    private final DeleteGalleryGroupUseCase deleteGalleryGroupUseCase;
    private final AddGalleryPhotosUseCase addGalleryPhotosUseCase;
    private final DeleteGalleryPhotoUseCase deleteGalleryPhotoUseCase;

    /**
     * GET /api/admin/gallery - Get all gallery groups with photo counts
     */
    @GetMapping(EndPoints.Admin.Gallery.GET_ALL)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetGalleryGroupsResponse>> getAllGroups() {
        return ResponseEntity.ok(getGalleryGroupsUseCase.execute());
    }

    /**
     * GET /api/admin/gallery/{groupId} - Get gallery group with all its photos
     */
    @GetMapping(EndPoints.Admin.Gallery.GET_BY_ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetGalleryGroupResponse> getGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(getGalleryGroupUseCase.execute(groupId));
    }

    /**
     * POST /api/admin/gallery - Create a new gallery group
     */
    @PostMapping(EndPoints.Admin.Gallery.CREATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostGalleryGroupResponse> createGroup(
            @Valid @RequestBody PostGalleryGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postGalleryGroupUseCase.execute(request));
    }

    /**
     * PATCH /api/admin/gallery/{groupId} - Update gallery group
     */
    @PatchMapping(EndPoints.Admin.Gallery.UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody PatchGalleryGroupRequest request) {
        patchGalleryGroupUseCase.execute(groupId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/admin/gallery/{groupId} - Delete gallery group and all its photos
     */
    @DeleteMapping(EndPoints.Admin.Gallery.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        deleteGalleryGroupUseCase.execute(groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/admin/gallery/{groupId}/photos - Upload photos to a group
     */
    @PostMapping(EndPoints.Admin.Gallery.Photos.ADD)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddGalleryPhotosResponse> addPhotos(
            @PathVariable Long groupId,
            @RequestPart("photos") List<MultipartFile> photos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addGalleryPhotosUseCase.execute(groupId, photos));
    }

    /**
     * DELETE /api/admin/gallery/{groupId}/photos/{photoId} - Delete a single photo
     */
    @DeleteMapping(EndPoints.Admin.Gallery.Photos.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long groupId,
            @PathVariable Long photoId) {
        deleteGalleryPhotoUseCase.execute(groupId, photoId);
        return ResponseEntity.noContent().build();
    }
}
