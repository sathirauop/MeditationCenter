package com.isipathana.meditationcenter.rest.event.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Presenter for transforming Event domain objects into GetEventsResponse DTOs.
 * Implements the ResponseBuilder interface to create paginated responses.
 * <p>
 * Generates presigned URLs for event images when R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetEventPresenter implements GetEventResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private GetEventsHttpDataAccess httpRepository;

    @Override
    public OffsetSearchResponse<GetEventsResponse> build(
            Stream<Event> events,
            long currentOffset,
            long maxOffset) {

        List<GetEventsResponse> responseList = events
                .map(this::mapEventToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    /**
     * Maps an Event domain object to GetEventsResponse DTO.
     * Generates presigned URLs for images if R2 is enabled.
     */
    private GetEventsResponse mapEventToResponse(Event event) {
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

        return GetEventsResponse.builder()
                .eventId(event.eventId())
                .name(event.name())
                .description(event.description())
                .nameSi(event.nameSi())
                .descriptionSi(event.descriptionSi())
                .eventDate(event.eventDate())
                .startTime(event.startTime())
                .endTime(event.endTime())
                .location(event.location())
                .coverImageUrl(coverImageUrl)
                .galleryImageUrls(galleryImageUrls.isEmpty() ? null : galleryImageUrls)
                .build();
    }
}
