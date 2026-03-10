package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;
import com.isipathana.meditationcenter.rest.event.get.GetEventsHttpDataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Presenter for PatchEvent endpoint.
 * Transforms Event domain object to PatchEventResponse DTO.
 * Generates presigned URLs for images when R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
public class PatchEventPresenter implements PatchEventResponseBuilder {

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private GetEventsHttpDataAccess httpRepository;

    @Override
    public PatchEventResponse build(Event event) {
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

        return new PatchEventResponse(
                event.eventId(),
                event.name(),
                event.description(),
                event.nameSi(),
                event.descriptionSi(),
                event.eventDate(),
                event.startTime(),
                event.endTime(),
                event.location(),
                coverImageUrl,
                galleryImageUrls.isEmpty() ? null : galleryImageUrls,
                event.isActive(),
                event.updatedAt());
    }
}
