package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming Event domain objects into GetEventsResponse DTOs.
 * Implements the ResponseBuilder interface to create paginated responses.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetEventPresenter implements GetEventResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    @Override
    public OffsetSearchResponse<GetEventsResponse> build(
            Stream<Event> events,
            long currentOffset,
            long maxOffset) {

        List<GetEventsResponse> responseList = events
                .map(event -> GetEventsResponse.builder()
                        .eventId(event.eventId())
                        .name(event.name())
                        .description(event.description())
                        .eventDate(event.eventDate())
                        .startTime(event.startTime())
                        .endTime(event.endTime())
                        .location(event.location())
                        .images(event.images())
                        .build())
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }
}
