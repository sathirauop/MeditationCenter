package com.isipathana.meditationcenter.rest.gallery;

import com.isipathana.meditationcenter.constants.EndPoints;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for public gallery endpoints.
 * No authentication required.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Gallery.BASE)
@RequiredArgsConstructor
public class GalleryController {

    private final GetPublicGalleryUseCase getPublicGalleryUseCase;

    /**
     * GET /api/gallery - Get all active gallery groups with their photos
     */
    @GetMapping(EndPoints.Gallery.GET_ALL)
    public ResponseEntity<List<GetPublicGalleryResponse>> getGallery() {
        return ResponseEntity.ok(getPublicGalleryUseCase.execute());
    }
}
