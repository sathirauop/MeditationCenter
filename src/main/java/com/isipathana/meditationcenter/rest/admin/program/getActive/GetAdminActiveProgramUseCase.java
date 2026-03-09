package com.isipathana.meditationcenter.rest.admin.program.getActive;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import com.isipathana.meditationcenter.rest.program.getActive.GetActiveProgramDataAccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * UseCase for getting the active meditation program (admin).
 * Reuses the public repository and builds admin response with full details.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAdminActiveProgramUseCase {

    private final GetActiveProgramDataAccess repository;

    @Autowired(required = false)
    private GetAdminActiveProgramHttpDataAccess httpRepository;

    @Transactional(readOnly = true)
    public GetAdminActiveProgramResponse execute() {
        log.info("Fetching active meditation program (admin)");

        MeditationProgram program = repository.findActiveProgram()
                .orElseThrow(() -> new ResourceNotFoundException("No active meditation program found"));

        log.info("Active program found: {}", program.name());

        // Generate presigned URLs
        String coverImageUrl = null;
        Set<String> galleryImageUrls = new HashSet<>();
        List<GetAdminActiveProgramResponse.GalleryImage> galleryImages = new ArrayList<>();

        if (httpRepository != null) {
            if (program.coverImageKey() != null) {
                coverImageUrl = httpRepository.generatePresignedUrl(program.coverImageKey());
            }

            if (program.galleryImageKeys() != null && !program.galleryImageKeys().isEmpty()) {
                Map<String, String> urlMap = httpRepository.generatePresignedUrls(program.galleryImageKeys());
                galleryImageUrls.addAll(urlMap.values());

                // Build gallery images list with key-url pairs for admin UI
                for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                    galleryImages.add(GetAdminActiveProgramResponse.GalleryImage.builder()
                            .key(entry.getKey())
                            .url(entry.getValue())
                            .build());
                }
            }
        }

        return GetAdminActiveProgramResponse.builder()
                .meditationProgramId(program.meditationProgramId())
                .name(program.name())
                .description(program.description())
                .nameSi(program.nameSi())
                .descriptionSi(program.descriptionSi())
                .maxSeats(program.maxSeats())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls.isEmpty() ? null : galleryImageUrls)
                .galleryImages(galleryImages.isEmpty() ? null : galleryImages)
                .isActive(program.isActive())
                .createdAt(program.createdAt())
                .updatedAt(program.updatedAt())
                .build();
    }
}
