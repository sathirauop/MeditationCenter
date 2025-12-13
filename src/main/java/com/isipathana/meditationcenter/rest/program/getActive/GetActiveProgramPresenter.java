package com.isipathana.meditationcenter.rest.program.getActive;

import com.isipathana.meditationcenter.records.program.MeditationProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for transforming MeditationProgram to GetActiveProgramResponse (public).
 * Generates presigned URLs for images if R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetActiveProgramPresenter implements GetActiveProgramResponseBuilder {

    @Autowired(required = false)
    private GetActiveProgramHttpDataAccess httpRepository;

    @Override
    public GetActiveProgramResponse build(MeditationProgram program) {
        String coverImageUrl = null;
        Set<String> galleryImageUrls = new HashSet<>();

        // Generate presigned URLs if R2 is enabled
        if (httpRepository != null) {
            if (program.coverImageKey() != null) {
                coverImageUrl = httpRepository.generatePresignedUrl(program.coverImageKey());
            }

            if (program.galleryImageKeys() != null && !program.galleryImageKeys().isEmpty()) {
                Map<String, String> urlMap = httpRepository.generatePresignedUrls(program.galleryImageKeys());
                galleryImageUrls.addAll(urlMap.values());
            }
        }

        return GetActiveProgramResponse.builder()
                .meditationProgramId(program.meditationProgramId())
                .name(program.name())
                .description(program.description())
                .maxSeats(program.maxSeats())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls.isEmpty() ? null : galleryImageUrls)
                .build();
    }
}
