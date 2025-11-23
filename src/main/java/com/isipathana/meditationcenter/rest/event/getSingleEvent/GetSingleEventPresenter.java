package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Presenter for transforming Event domain object into GetSingleEventResponse DTO.
 * Implements the ResponseBuilder interface to create the response.
 * <p>
 * Generates presigned URLs for event images when R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetSingleEventPresenter implements GetSingleEventResponseBuilder {

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private GetSingleEventHttpDataAccess httpRepository;

    @Override
    public GetSingleEventResponse build(Event event) {
        String coverImageUrl = null;
        Set<String> galleryImageUrls = new HashSet<>();

        // Generate presigned URLs if R2 is enabled and image keys exist
        if (httpRepository != null) {
            // Generate cover image URL
            if (event.coverImageKey() != null && !event.coverImageKey().isEmpty()) {
                coverImageUrl = httpRepository.generatePresignedUrl(event.coverImageKey());
            }

            // Generate gallery image URLs
            if (event.galleryImageKeys() != null && !event.galleryImageKeys().isEmpty()) {
                var urlMap = httpRepository.generatePresignedUrls(event.galleryImageKeys());
                galleryImageUrls.addAll(urlMap.values());
            }
        }

        return GetSingleEventResponse.builder()
                .eventId(event.eventId())
                .name(event.name())
                .description(event.description())
                .eventDate(event.eventDate())
                .startTime(event.startTime())
                .endTime(event.endTime())
                .location(event.location())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls.isEmpty() ? null : galleryImageUrls)
                .build();
    }
}
