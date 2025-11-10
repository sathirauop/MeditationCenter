package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for GetEventsUseCase.
 * Tests business logic using mocked dependencies.
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class GetEventsUseCaseTest {

    @Mock
    private GetEventsDataAccess repository;

    @Mock
    private GetEventResponseBuilder responseBuilder;

    @InjectMocks
    private GetEventsUseCase useCase;

    @AfterEach
    void tearDown() {
        Mockito.reset(repository, responseBuilder);
    }

    @Test
    void shouldReturnPaginatedResponse_whenEventsExist() {
        // Arrange
        GetEventsRequest request = new GetEventsRequest(20, 0);

        List<Event> mockEvents = List.of(
                createMockEvent(1L, "Event 1"),
                createMockEvent(2L, "Event 2")
        );

        List<GetEventsResponse> mockResponses = List.of(
                createMockResponse(1L, "Event 1"),
                createMockResponse(2L, "Event 2")
        );

        OffsetSearchResponse<GetEventsResponse> expectedResponse =
                new OffsetSearchResponse<>(mockResponses, 0, 2);

        when(repository.findActiveEvents(0, 20)).thenReturn(mockEvents);
        when(repository.getActiveEventCount()).thenReturn(2L);
        when(responseBuilder.build(any(), eq(0L), eq(2L))).thenReturn(expectedResponse);

        // Act
        OffsetSearchResponse<GetEventsResponse> actualResponse = useCase.handle(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(2, actualResponse.getData().size());
        assertEquals(0, actualResponse.getCurrentOffset());
        assertEquals(2, actualResponse.getMaxOffset());

        // Verify interactions
        verify(repository).findActiveEvents(0, 20);
        verify(repository).getActiveEventCount();
        verify(responseBuilder).build(any(), eq(0L), eq(2L));
    }

    @Test
    void shouldCalculateOffsetCorrectly() {
        // Arrange - page 2 with limit 10 means offset should be 10
        GetEventsRequest request = new GetEventsRequest(10, 1);

        when(repository.findActiveEvents(10, 10)).thenReturn(List.of());
        when(repository.getActiveEventCount()).thenReturn(15L);
        when(responseBuilder.build(any(), eq(10L), eq(15L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 10, 15));

        // Act
        useCase.handle(request);

        // Assert
        verify(repository).findActiveEvents(10, 10); // offset * limit = 1 * 10 = 10
        verify(responseBuilder).build(any(), eq(10L), eq(15L));
    }

    @Test
    void shouldPassEventsStreamToPresenter() {
        // Arrange
        GetEventsRequest request = new GetEventsRequest(5, 0);

        List<Event> mockEvents = List.of(
                createMockEvent(1L, "Event 1"),
                createMockEvent(2L, "Event 2"),
                createMockEvent(3L, "Event 3")
        );

        when(repository.findActiveEvents(0, 5)).thenReturn(mockEvents);
        when(repository.getActiveEventCount()).thenReturn(3L);
        when(responseBuilder.build(any(), anyLong(), anyLong()))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 0, 3));

        // Act
        useCase.handle(request);

        // Assert - capture the stream argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Stream<Event>> streamCaptor = ArgumentCaptor.forClass(Stream.class);
        verify(responseBuilder).build(streamCaptor.capture(), eq(0L), eq(3L));

        // Verify stream contains expected events
        List<Event> streamedEvents = streamCaptor.getValue().toList();
        assertEquals(3, streamedEvents.size());
    }

    @Test
    void shouldReturnEmptyResponse_whenNoEvents() {
        // Arrange
        GetEventsRequest request = new GetEventsRequest(20, 0);

        when(repository.findActiveEvents(0, 20)).thenReturn(List.of());
        when(repository.getActiveEventCount()).thenReturn(0L);
        when(responseBuilder.build(any(), eq(0L), eq(0L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 0, 0));

        // Act
        OffsetSearchResponse<GetEventsResponse> response = useCase.handle(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        assertEquals(0, response.getMaxOffset());
    }

    @Test
    void shouldHandleLargeOffset() {
        // Arrange - offset=10, limit=20, so actual offset = 200
        GetEventsRequest request = new GetEventsRequest(20, 10);

        when(repository.findActiveEvents(200, 20)).thenReturn(List.of());
        when(repository.getActiveEventCount()).thenReturn(250L);
        when(responseBuilder.build(any(), eq(200L), eq(250L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 200, 250));

        // Act
        useCase.handle(request);

        // Assert
        verify(repository).findActiveEvents(200, 20);
    }

    // Helper methods
    private Event createMockEvent(Long id, String name) {
        return Event.builder()
                .eventId(id)
                .name(name)
                .description("Description for " + name)
                .eventDate(LocalDate.of(2024, 6, 1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .location("Test Location")
//                .images("/images/test.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .build();
    }

    private GetEventsResponse createMockResponse(Long id, String name) {
        return GetEventsResponse.builder()
                .eventId(id)
                .name(name)
                .description("Description for " + name)
                .eventDate(LocalDate.of(2024, 6, 1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .location("Test Location")
//                .images("/images/test.jpg")
                .build();
    }
}
