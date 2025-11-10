package com.isipathana.meditationcenter.rest.events.admin.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import com.isipathana.meditationcenter.rest.event.GetEventsHttpDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Presenter for GetAdminEvents endpoint.
 * Transforms Event domain objects to GetAdminEventsResponse DTOs.
 * <p>
 * Generates presigned URLs for event images when R2 is enabled.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetAdminEventsPresenter implements GetAdminEventsResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private GetEventsHttpDataAccess httpRepository;

    @Override
    public OffsetSearchResponse<GetAdminEventsResponse> build(
            Stream<Event> events,
            long currentOffset,
            long maxOffset) {

        List<GetAdminEventsResponse> responseList = events
                .map(this::mapEventToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    /**
     * Maps an Event domain object to GetAdminEventsResponse DTO.
     * Generates presigned URLs for images if R2 is enabled.
     */
    private GetAdminEventsResponse mapEventToResponse(Event event) {
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

        return GetAdminEventsResponse.builder()
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
