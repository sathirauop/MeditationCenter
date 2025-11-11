package com.isipathana.meditationcenter.rest.admin.event.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.event.Event;
import com.isipathana.meditationcenter.rest.event.get.GetEventsHttpDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for GetAdminEventsPresenter.
 * Tests transformation of Event domain objects to GetAdminEventsResponse DTOs with image URL generation.
 * <p>
 * Pattern: Mock factory and HTTP repository (R2 operations).
 * Reference: bow-products presenter tests
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class GetAdminEventsPresenterTest {

    @Mock
    private OffsetSearchResponse.Factory factory;

    @Mock
    private GetEventsHttpDataAccess httpRepository;

    @InjectMocks
    private GetAdminEventsPresenter presenter;

    @AfterEach
    void tearDown() {
        Mockito.reset(factory, httpRepository);
    }

//    @Test
//    void shouldBuildResponse_withValidEvents() {
//        // Arrange
//        Event event1 = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Celebrate Buddha's birth")
//                .eventDate(LocalDate.of(2024, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .coverImageKey("events/1/cover/wesak.jpg")
//                .galleryImageKeys(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg"))
//                .isActive(true)
//                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
//                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
//                .build();
//
//        Event event2 = Event.builder()
//                .eventId(2L)
//                .name("Meditation Retreat")
//                .description("3-day retreat")
//                .eventDate(LocalDate.of(2024, 6, 10))
//                .startTime(LocalTime.of(9, 0))
//                .endTime(LocalTime.of(17, 0))
//                .location("Retreat Center")
//                .coverImageKey(null)
//                .galleryImageKeys(null)
//                .isActive(true)
//                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
//                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
//                .build();
//
//        Stream<Event> eventStream = Stream.of(event1, event2);
//
//        // Mock HTTP repository responses
//        when(httpRepository.generatePresignedUrl("events/1/cover/wesak.jpg"))
//                .thenReturn("https://r2.example.com/wesak.jpg?token=abc");
//        when(httpRepository.generatePresignedUrls(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg")))
//                .thenReturn(Map.of(
//                        "events/1/gallery/img1.jpg", "https://r2.example.com/img1.jpg?token=def",
//                        "events/1/gallery/img2.jpg", "https://r2.example.com/img2.jpg?token=ghi"
//                ));
//
//        GetAdminEventsResponse response1 = GetAdminEventsResponse.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Celebrate Buddha's birth")
//                .eventDate(LocalDate.of(2024, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .coverImageUrl("https://r2.example.com/wesak.jpg?token=abc")
//                .galleryImageUrls(Set.of(
//                        "https://r2.example.com/img1.jpg?token=def",
//                        "https://r2.example.com/img2.jpg?token=ghi"
//                ))
//                .build();
//
//        GetAdminEventsResponse response2 = GetAdminEventsResponse.builder()
//                .eventId(2L)
//                .name("Meditation Retreat")
//                .description("3-day retreat")
//                .eventDate(LocalDate.of(2024, 6, 10))
//                .startTime(LocalTime.of(9, 0))
//                .endTime(LocalTime.of(17, 0))
//                .location("Retreat Center")
//                .coverImageUrl(null)
//                .galleryImageUrls(null)
//                .build();
//
//        when(factory.create(anyList(), eq(0L), eq(2L)))
//                .thenAnswer(invocation -> new OffsetSearchResponse<>(List.of(response1, response2), 0, 2));
//
//        // Act
//        OffsetSearchResponse<GetAdminEventsResponse> actualResponse = presenter.build(eventStream, 0, 2);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(2, actualResponse.getData().size());
//
//        verify(httpRepository).generatePresignedUrl("events/1/cover/wesak.jpg");
//        verify(httpRepository).generatePresignedUrls(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg"));
//        verify(factory).create(anyList(), eq(0L), eq(2L));
//    }

//    @Test
//    void shouldMapAllEventFields() {
//        // Arrange
//        Event event = Event.builder()
//                .eventId(100L)
//                .name("Test Event")
//                .description("Test Description")
//                .eventDate(LocalDate.of(2024, 12, 25))
//                .startTime(LocalTime.of(14, 30))
//                .endTime(LocalTime.of(16, 45))
//                .location("Test Location")
//                .coverImageKey("events/100/cover/test.jpg")
//                .galleryImageKeys(null)
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(httpRepository.generatePresignedUrl("events/100/cover/test.jpg"))
//                .thenReturn("https://r2.example.com/test.jpg?token=xyz");
//
//        when(factory.create(anyList(), anyLong(), anyLong()))
//                .thenAnswer(invocation -> new OffsetSearchResponse<>(invocation.getArgument(0), 0, 1));
//
//        // Act
//        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(Stream.of(event), 0, 1);
//
//        // Assert
//        assertFalse(response.getData().isEmpty());
//        GetAdminEventsResponse mappedEvent = response.getData().get(0);
//
//        assertEquals(100L, mappedEvent.eventId());
//        assertEquals("Test Event", mappedEvent.name());
//        assertEquals("Test Description", mappedEvent.description());
//        assertEquals(LocalDate.of(2024, 12, 25), mappedEvent.eventDate());
//        assertEquals(LocalTime.of(14, 30), mappedEvent.startTime());
//        assertEquals(LocalTime.of(16, 45), mappedEvent.endTime());
//        assertEquals("Test Location", mappedEvent.location());
//        assertEquals("https://r2.example.com/test.jpg?token=xyz", mappedEvent.coverImageUrl());
//        assertNull(mappedEvent.galleryImageUrls());
//    }

    @Test
    void shouldHandleEmptyStream() {
        // Arrange
        Stream<Event> emptyStream = Stream.empty();

        when(factory.create(eq(List.of()), eq(0L), eq(0L)))
                .thenReturn(new OffsetSearchResponse<>(List.of(), 0, 0));

        // Act
        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(emptyStream, 0, 0);

        // Assert
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        assertEquals(0, response.getCurrentOffset());
        assertEquals(0, response.getMaxOffset());
    }

    @Test
    void shouldHandleNullImageKeys() {
        // Arrange
        Event event = Event.builder()
                .eventId(1L)
                .name("Event Without Images")
                .description("Desc")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .location("Loc")
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(factory.create(anyList(), eq(0L), eq(1L)))
                .thenAnswer(invocation -> new OffsetSearchResponse<>(invocation.getArgument(0), 0, 1));

        // Act
        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(Stream.of(event), 0, 1);

        // Assert
        assertFalse(response.getData().isEmpty());
        GetAdminEventsResponse mappedEvent = response.getData().get(0);

        assertNull(mappedEvent.coverImageUrl());
        assertNull(mappedEvent.galleryImageUrls());
    }

    @Test
    void shouldHandleEmptyImageKeys() {
        // Arrange
        Event event = Event.builder()
                .eventId(1L)
                .name("Event With Empty Keys")
                .description("Desc")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .location("Loc")
                .coverImageKey("")
                .galleryImageKeys(Set.of())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(factory.create(anyList(), eq(0L), eq(1L)))
                .thenAnswer(invocation -> new OffsetSearchResponse<>(invocation.getArgument(0), 0, 1));

        // Act
        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(Stream.of(event), 0, 1);

        // Assert
        assertFalse(response.getData().isEmpty());
        GetAdminEventsResponse mappedEvent = response.getData().get(0);

        assertNull(mappedEvent.coverImageUrl());
        assertNull(mappedEvent.galleryImageUrls());
    }

//    @Test
//    void shouldGenerateGalleryUrls_whenGalleryImagesExist() {
//        // Arrange
//        Event event = Event.builder()
//                .eventId(1L)
//                .name("Event With Gallery")
//                .description("Desc")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .location("Loc")
//                .coverImageKey(null)
//                .galleryImageKeys(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg"))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(httpRepository.generatePresignedUrls(anySet()))
//                .thenReturn(Map.of(
//                        "events/1/gallery/img1.jpg", "https://r2.example.com/img1.jpg?token=aaa",
//                        "events/1/gallery/img2.jpg", "https://r2.example.com/img2.jpg?token=bbb"
//                ));
//
//        when(factory.create(anyList(), eq(0L), eq(1L)))
//                .thenAnswer(invocation -> new OffsetSearchResponse<>(invocation.getArgument(0), 0, 1));
//
//        // Act
//        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(Stream.of(event), 0, 1);
//
//        // Assert
//        assertFalse(response.getData().isEmpty());
//        GetAdminEventsResponse mappedEvent = response.getData().get(0);
//
//        assertNull(mappedEvent.coverImageUrl());
//        assertNotNull(mappedEvent.galleryImageUrls());
//        assertEquals(2, mappedEvent.galleryImageUrls().size());
//        assertTrue(mappedEvent.galleryImageUrls().contains("https://r2.example.com/img1.jpg?token=aaa"));
//        assertTrue(mappedEvent.galleryImageUrls().contains("https://r2.example.com/img2.jpg?token=bbb"));
//    }

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
                        .coverImageKey(null)
                        .galleryImageKeys(null)
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

    @Test
    void shouldHandlePaginationCorrectly() {
        // Arrange
        Event event1 = Event.builder()
                .eventId(1L)
                .name("Event 1")
                .description("Desc 1")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .location("Loc 1")
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Event event2 = Event.builder()
                .eventId(2L)
                .name("Event 2")
                .description("Desc 2")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .location("Loc 2")
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Stream<Event> eventStream = Stream.of(event1, event2);

        when(factory.create(anyList(), eq(20L), eq(50L)))
                .thenAnswer(invocation -> {
                    List<GetAdminEventsResponse> list = invocation.getArgument(0);
                    return new OffsetSearchResponse<>(list, 20, 50);
                });

        // Act
        OffsetSearchResponse<GetAdminEventsResponse> response = presenter.build(eventStream, 20, 50);

        // Assert
        assertEquals(20, response.getCurrentOffset());
        assertEquals(50, response.getMaxOffset());
        assertEquals(2, response.getData().size());
    }
}
