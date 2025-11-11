package com.isipathana.meditationcenter.rest.admin.event.post;

import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for PostEventUseCase.
 * Tests business logic for event creation with optional image uploads.
 * <p>
 * Pattern: Mock all dependencies, test business logic paths.
 * Reference: bow-products PostUserFavoriteUseCaseTest
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class PostEventUseCaseTest {

    @Mock
    private PostEventDataAccess repository;

    @Mock
    private PostEventHttpDataAccess httpRepository;

    @InjectMocks
    private PostEventUseCase useCase;

    @AfterEach
    void tearDown() {
        reset(repository, httpRepository);
    }

    @Test
    void shouldCreateEvent_whenDataIsValid() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Wesak Celebration",
                "Annual Wesak ceremony",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Main Hall",
                true
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Wesak Celebration")
                .description("Annual Wesak ceremony")
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Main Hall")
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .updatedAt(LocalDateTime.of(2024, 5, 21, 8, 30))
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        PostEventResponse response = useCase.execute(request, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.eventId());
        assertEquals("Wesak Celebration", response.name());
        assertEquals("Annual Wesak ceremony", response.description());
        assertEquals(LocalDate.of(2025, 5, 15), response.eventDate());
        assertEquals(LocalTime.of(6, 0), response.startTime());
        assertEquals(LocalTime.of(20, 0), response.endTime());
        assertEquals("Main Hall", response.location());
        assertEquals(true, response.isActive());
        assertNull(response.coverImageKey());
        assertNull(response.galleryImageKeys());

        verify(repository).createEvent(any(Event.class));
        verifyNoInteractions(httpRepository);
    }

    @Test
    void shouldThrowException_whenEndTimeBeforeStartTime() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Invalid Event",
                "End time before start time",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(20, 0),  // Start time
                LocalTime.of(6, 0),   // End time (before start time!)
                "Main Hall",
                true
        );

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> useCase.execute(request, null, null));

        assertEquals("End time must be after start time", exception.getMessage());
        verifyNoInteractions(repository);
        verifyNoInteractions(httpRepository);
    }

    @Test
    void shouldThrowException_whenEndTimeEqualsStartTime() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Invalid Event",
                "Same start and end time",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(10, 0),
                LocalTime.of(10, 0),  // Same as start time
                "Main Hall",
                true
        );

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> useCase.execute(request, null, null));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldSetIsActiveToTrue_whenIsActiveIsNull() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Event",
                "Description",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Location",
                null  // isActive is null
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Event")
                .description("Description")
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Location")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        PostEventResponse response = useCase.execute(request);

        // Assert
        assertTrue(response.isActive());
        verify(repository).createEvent(argThat(event ->
                event.isActive() != null && event.isActive()
        ));
    }

//    @Test
//    void shouldUploadCoverImage_whenCoverImageProvided() {
//        // Arrange
//        PostEventRequest request = new PostEventRequest(
//                "Wesak Celebration",
//                "Annual Wesak ceremony",
//                LocalDate.of(2025, 5, 15),
//                LocalTime.of(6, 0),
//                LocalTime.of(20, 0),
//                "Main Hall",
//                true
//        );
//
//        MultipartFile coverImage = mock(MultipartFile.class);
//        when(coverImage.isEmpty()).thenReturn(false);
//
//        Event createdEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Annual Wesak ceremony")
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        Event updatedEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Annual Wesak ceremony")
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .coverImageKey("events/1/cover/abc123.jpg")
//                .galleryImageKeys(null)
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);
//        when(httpRepository.uploadCoverImage(eq(1L), eq(coverImage)))
//                .thenReturn("events/1/cover/abc123.jpg");
//        when(repository.updateImageKeys(eq(1L), eq("events/1/cover/abc123.jpg"), isNull()))
//                .thenReturn(updatedEvent);
//
//        // Act
//        PostEventResponse response = useCase.execute(request, coverImage, null);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("events/1/cover/abc123.jpg", response.coverImageKey());
//        assertNull(response.galleryImageKeys());
//
//        verify(repository).createEvent(any(Event.class));
//        verify(httpRepository).uploadCoverImage(1L, coverImage);
//        verify(repository).updateImageKeys(1L, "events/1/cover/abc123.jpg", null);
//    }

//    @Test
//    void shouldUploadGalleryImages_whenGalleryImagesProvided() {
//        // Arrange
//        PostEventRequest request = new PostEventRequest(
//                "Wesak Celebration",
//                "Annual Wesak ceremony",
//                LocalDate.of(2025, 5, 15),
//                LocalTime.of(6, 0),
//                LocalTime.of(20, 0),
//                "Main Hall",
//                true
//        );
//
//        MultipartFile galleryImage1 = mock(MultipartFile.class);
//        MultipartFile galleryImage2 = mock(MultipartFile.class);
//        when(galleryImage1.isEmpty()).thenReturn(false);
//        when(galleryImage2.isEmpty()).thenReturn(false);
//        List<MultipartFile> galleryImages = List.of(galleryImage1, galleryImage2);
//
//        Event createdEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Annual Wesak ceremony")
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        List<String> uploadedKeys = List.of(
//                "events/1/gallery/img1.jpg",
//                "events/1/gallery/img2.jpg"
//        );
//
//        Event updatedEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Annual Wesak ceremony")
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .coverImageKey(null)
//                .galleryImageKeys(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg"))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);
//        when(httpRepository.uploadGalleryImages(eq(1L), eq(galleryImages)))
//                .thenReturn(uploadedKeys);
//        when(repository.updateImageKeys(eq(1L), isNull(), anySet()))
//                .thenReturn(updatedEvent);
//
//        // Act
//        PostEventResponse response = useCase.execute(request, null, galleryImages);
//
//        // Assert
//        assertNotNull(response);
//        assertNull(response.coverImageKey());
//        assertNotNull(response.galleryImageKeys());
//        assertEquals(2, response.galleryImageKeys().size());
//        assertTrue(response.galleryImageKeys().contains("events/1/gallery/img1.jpg"));
//        assertTrue(response.galleryImageKeys().contains("events/1/gallery/img2.jpg"));
//
//        verify(httpRepository).uploadGalleryImages(1L, galleryImages);
//        verify(repository).updateImageKeys(eq(1L), isNull(), anySet());
//    }

//    @Test
//    void shouldUploadBothImageTypes_whenBothProvided() {
//        // Arrange
//        PostEventRequest request = new PostEventRequest(
//                "Wesak Celebration",
//                "Annual Wesak ceremony",
//                LocalDate.of(2025, 5, 15),
//                LocalTime.of(6, 0),
//                LocalTime.of(20, 0),
//                "Main Hall",
//                true
//        );
//
//        MultipartFile coverImage = mock(MultipartFile.class);
//        when(coverImage.isEmpty()).thenReturn(false);
//
//        MultipartFile galleryImage = mock(MultipartFile.class);
//        when(galleryImage.isEmpty()).thenReturn(false);
//        List<MultipartFile> galleryImages = List.of(galleryImage);
//
//        Event createdEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .isActive(true)
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        Event updatedEvent = Event.builder()
//                .eventId(1L)
//                .name("Wesak Celebration")
//                .description("Annual Wesak ceremony")
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .location("Main Hall")
//                .coverImageKey("events/1/cover/cover.jpg")
//                .galleryImageKeys(Set.of("events/1/gallery/img1.jpg"))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);
//        when(httpRepository.uploadCoverImage(1L, coverImage)).thenReturn("events/1/cover/cover.jpg");
//        when(httpRepository.uploadGalleryImages(1L, galleryImages))
//                .thenReturn(List.of("events/1/gallery/img1.jpg"));
//        when(repository.updateImageKeys(eq(1L), eq("events/1/cover/cover.jpg"), anySet()))
//                .thenReturn(updatedEvent);
//
//        // Act
//        PostEventResponse response = useCase.execute(request, coverImage, galleryImages);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("events/1/cover/cover.jpg", response.coverImageKey());
//        assertNotNull(response.galleryImageKeys());
//        assertEquals(1, response.galleryImageKeys().size());
//
//        verify(httpRepository).uploadCoverImage(1L, coverImage);
//        verify(httpRepository).uploadGalleryImages(1L, galleryImages);
//        verify(repository).updateImageKeys(eq(1L), eq("events/1/cover/cover.jpg"), anySet());
//    }

    @Test
    void shouldSkipImageUpload_whenCoverImageIsNull() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Event",
                "Description",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Location",
                true
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Event")
                .isActive(true)
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        useCase.execute(request, null, null);

        // Assert
        verify(repository).createEvent(any(Event.class));
        verify(repository, never()).updateImageKeys(anyLong(), anyString(), anySet());
        verifyNoInteractions(httpRepository);
    }

//    @Test
//    void shouldSkipImageUpload_whenCoverImageIsEmpty() {
//        // Arrange
//        PostEventRequest request = new PostEventRequest(
//                "Event",
//                "Description",
//                LocalDate.of(2025, 5, 15),
//                LocalTime.of(6, 0),
//                LocalTime.of(20, 0),
//                "Location",
//                true
//        );
//
//        MultipartFile coverImage = mock(MultipartFile.class);
//        when(coverImage.isEmpty()).thenReturn(true);
//
//        Event createdEvent = Event.builder()
//                .eventId(1L)
//                .name("Event")
//                .isActive(true)
//                .eventDate(LocalDate.of(2025, 5, 15))
//                .startTime(LocalTime.of(6, 0))
//                .endTime(LocalTime.of(20, 0))
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);
//
//        // Act
//        useCase.execute(request, coverImage, null);
//
//        // Assert
//        verify(repository).createEvent(any(Event.class));
//        verify(httpRepository, never()).uploadCoverImage(anyLong(), any());
//        verify(repository, never()).updateImageKeys(anyLong(), anyString(), anySet());
//    }

    @Test
    void shouldSkipImageUpload_whenGalleryImagesIsNull() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Event",
                "Description",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Location",
                true
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Event")
                .isActive(true)
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        useCase.execute(request, null, null);

        // Assert
        verifyNoInteractions(httpRepository);
    }

    @Test
    void shouldSkipImageUpload_whenGalleryImagesIsEmpty() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Event",
                "Description",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Location",
                true
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Event")
                .isActive(true)
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        useCase.execute(request, null, List.of());

        // Assert
        verify(httpRepository, never()).uploadGalleryImages(anyLong(), anyList());
    }

    @Test
    void shouldUseBackwardCompatibleExecuteMethod() {
        // Arrange
        PostEventRequest request = new PostEventRequest(
                "Event",
                "Description",
                LocalDate.of(2025, 5, 15),
                LocalTime.of(6, 0),
                LocalTime.of(20, 0),
                "Location",
                true
        );

        Event createdEvent = Event.builder()
                .eventId(1L)
                .name("Event")
                .description("Description")
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Location")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.createEvent(any(Event.class))).thenReturn(createdEvent);

        // Act
        PostEventResponse response = useCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("Event", response.name());
        verify(repository).createEvent(any(Event.class));
    }
}
