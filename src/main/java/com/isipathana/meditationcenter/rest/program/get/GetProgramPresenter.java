package com.isipathana.meditationcenter.rest.program.get;

import com.isipathana.meditationcenter.records.program.MeditationProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Presenter for transforming MeditationProgram to GetProgramResponse (public).
 * Generates presigned URLs for images if R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetProgramPresenter implements GetProgramResponseBuilder {

    @Autowired(required = false)
    private GetProgramHttpDataAccess httpRepository;

    @Override
    public GetProgramResponse build(MeditationProgram program) {
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

        return GetProgramResponse.builder()
                .meditationProgramId(program.meditationProgramId())
                .name(program.name())
                .description(program.description())
                .maxSeats(program.maxSeats())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls.isEmpty() ? null : galleryImageUrls)
                .build();
    }
}
