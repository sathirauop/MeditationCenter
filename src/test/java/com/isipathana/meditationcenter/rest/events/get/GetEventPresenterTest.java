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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for GetEventPresenter.
 * Tests transformation of Event domain objects to GetEventsResponse DTOs.
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class GetEventPresenterTest {

    @Mock
    private OffsetSearchResponse.Factory factory;

    @InjectMocks
    private GetEventPresenter presenter;

    @AfterEach
    void tearDown() {
        Mockito.reset(factory);
    }

    @Test
    void shouldBuildResponse_withValidEvents() {
        // Arrange
        Event event1 = Event.builder()
                .eventId(1L)
                .name("Wesak Celebration")
                .description("Celebrate Buddha's birth")
                .eventDate(LocalDate.of(2024, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Main Hall")
                .images("/images/wesak.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .build();

        Event event2 = Event.builder()
                .eventId(2L)
                .name("Meditation Retreat")
                .description("3-day retreat")
                .eventDate(LocalDate.of(2024, 6, 10))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .location("Retreat Center")
                .images("/images/retreat.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .build();

        Stream<Event> eventStream = Stream.of(event1, event2);

        GetEventsResponse response1 = GetEventsResponse.builder()
                .eventId(1L)
                .name("Wesak Celebration")
                .description("Celebrate Buddha's birth")
                .eventDate(LocalDate.of(2024, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Main Hall")
                .images("/images/wesak.jpg")
                .build();

        GetEventsResponse response2 = GetEventsResponse.builder()
                .eventId(2L)
                .name("Meditation Retreat")
                .description("3-day retreat")
                .eventDate(LocalDate.of(2024, 6, 10))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .location("Retreat Center")
                .images("/images/retreat.jpg")
                .build();

        OffsetSearchResponse<GetEventsResponse> expectedResponse =
                new OffsetSearchResponse<>(List.of(response1, response2), 0, 2);

        when(factory.create(eq(List.of(response1, response2)), eq(0L), eq(2L)))
                .thenReturn(expectedResponse);

        // Act
        OffsetSearchResponse<GetEventsResponse> actualResponse = presenter.build(eventStream, 0, 2);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GetEventsResponse>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(factory).create(listCaptor.capture(), eq(0L), eq(2L));

        List<GetEventsResponse> capturedList = listCaptor.getValue();
        assertEquals(2, capturedList.size());
        assertEquals("Wesak Celebration", capturedList.get(0).name());
        assertEquals("Meditation Retreat", capturedList.get(1).name());
    }

    @Test
    void shouldMapAllEventFields() {
        // Arrange
        Event event = Event.builder()
                .eventId(100L)
                .name("Test Event")
                .description("Test Description")
                .eventDate(LocalDate.of(2024, 12, 25))
                .startTime(LocalTime.of(14, 30))
                .endTime(LocalTime.of(16, 45))
                .location("Test Location")
                .images("/images/test1.jpg,/images/test2.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(factory.create(anyList(), anyLong(), anyLong()))
                .thenAnswer(invocation -> new OffsetSearchResponse<>(invocation.getArgument(0), 0, 1));

        // Act
        OffsetSearchResponse<GetEventsResponse> response = presenter.build(Stream.of(event), 0, 1);

        // Assert
        assertFalse(response.getData().isEmpty());
        GetEventsResponse mappedEvent = response.getData().get(0);

        assertEquals(100L, mappedEvent.eventId());
        assertEquals("Test Event", mappedEvent.name());
        assertEquals("Test Description", mappedEvent.description());
        assertEquals(LocalDate.of(2024, 12, 25), mappedEvent.eventDate());
        assertEquals(LocalTime.of(14, 30), mappedEvent.startTime());
        assertEquals(LocalTime.of(16, 45), mappedEvent.endTime());
        assertEquals("Test Location", mappedEvent.location());
        assertEquals("/images/test1.jpg,/images/test2.jpg", mappedEvent.images());
    }

    @Test
    void shouldHandleEmptyStream() {
        // Arrange
        Stream<Event> emptyStream = Stream.empty();

        when(factory.create(eq(List.of()), eq(0L), eq(0L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 0, 0));

        // Act
        OffsetSearchResponse<GetEventsResponse> response = presenter.build(emptyStream, 0, 0);

        // Assert
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        assertEquals(0, response.getCurrentOffset());
        assertEquals(0, response.getMaxOffset());
    }

    @Test
    void shouldUseFactoryToCreateResponse() {
        // Arrange
        Stream<Event> eventStream = Stream.of(
                Event.builder()
                        .eventId(1L)
                        .name("Event")
                        .description("Desc")
                        .eventDate(LocalDate.now())
                        .startTime(LocalTime.now())
                        .endTime(LocalTime.now())
                        .location("Loc")
                        .images("")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        when(factory.create(anyList(), eq(10L), eq(100L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 10, 100));

        // Act
        presenter.build(eventStream, 10, 100);

        // Assert
        verify(factory).create(anyList(), eq(10L), eq(100L));
    }
}
