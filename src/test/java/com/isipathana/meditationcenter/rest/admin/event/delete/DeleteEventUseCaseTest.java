package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.cache.EventImageUrlsCache;
import com.isipathana.meditationcenter.records.event.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test for DeleteEventUseCase.
 * Tests business logic for event deletion (hard delete) with R2 image cleanup.
 * <p>
 * Pattern: Mock all dependencies, test deletion flow and edge cases.
 * Reference: bow-products UseCase test patterns
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class DeleteEventUseCaseTest {

    @Mock
    private DeleteEventDataAccess repository;

    @Mock
    private EventImageUrlsCache imageUrlsCache;

    @Mock
    private DeleteEventHttpDataAccess httpRepository;

    @InjectMocks
    private DeleteEventUseCase useCase;

    @AfterEach
    void tearDown() {
        reset(repository, imageUrlsCache, httpRepository);
    }

    @Test
    void shouldDeleteEvent_whenEventExists() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Wesak Celebration")
                .description("Annual ceremony")
                .eventDate(LocalDate.of(2025, 5, 15))
                .startTime(LocalTime.of(6, 0))
                .endTime(LocalTime.of(20, 0))
                .location("Main Hall")
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(repository.deleteEvent(eventId)).thenReturn(true);

        // Act
        DeleteEventResponse response = useCase.execute(eventId);

        // Assert
        assertNotNull(response);
        assertEquals(eventId, response.eventId());
        assertTrue(response.message().contains("successfully deleted"));
        assertTrue(response.message().contains("Wesak Celebration"));

        verify(repository).findEventById(eventId);
        verify(repository).deleteEvent(eventId);
        verifyNoInteractions(httpRepository);
        verifyNoInteractions(imageUrlsCache);
    }

    @Test
    void shouldReturnNotFound_whenEventDoesntExist() {
        // Arrange
        Long eventId = 999L;
        when(repository.findEventById(eventId)).thenReturn(Optional.empty());

        // Act
        DeleteEventResponse response = useCase.execute(eventId);

        // Assert
        assertNotNull(response);
        assertEquals(eventId, response.eventId());
        assertTrue(response.message().contains("not found"));
        assertFalse(response.imagesDeleted());

        verify(repository).findEventById(eventId);
        verify(repository, never()).deleteEvent(anyLong());
        verifyNoInteractions(httpRepository);
    }

//    @Test
//    void shouldDeleteImagesFromR2_whenR2Enabled() {
//        // Arrange
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .eventId(eventId)
//                .name("Event with Images")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .location("Hall")
//                .coverImageKey("events/1/cover/img.jpg")
//                .galleryImageKeys(Set.of("events/1/gallery/img1.jpg", "events/1/gallery/img2.jpg"))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
//        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
//        when(repository.deleteEvent(eventId)).thenReturn(true);
//
//        // Act
//        DeleteEventResponse response = useCase.execute(eventId);
//
//        // Assert
//        assertTrue(response.imagesDeleted());
//        verify(httpRepository).deleteEventFolder(eventId);
//        verify(imageUrlsCache).invalidate("events/1/cover/img.jpg");
//        verify(imageUrlsCache).invalidate("events/1/gallery/img1.jpg");
//        verify(imageUrlsCache).invalidate("events/1/gallery/img2.jpg");
//        verify(repository).deleteEvent(eventId);
//    }

//    @Test
//    void shouldInvalidateCoverImageCache_whenCoverImageExists() {
//        // Arrange
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .eventId(eventId)
//                .name("Event")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .coverImageKey("events/1/cover/cover.jpg")
//                .galleryImageKeys(null)
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
//        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
//        when(repository.deleteEvent(eventId)).thenReturn(true);
//
//        // Act
//        useCase.execute(eventId);
//
//        // Assert
//        verify(imageUrlsCache).invalidate("events/1/cover/cover.jpg");
//        verify(imageUrlsCache, times(1)).invalidate(anyString()); // Only cover image
//    }

//    @Test
//    void shouldInvalidateGalleryImagesCaches_whenGalleryImagesExist() {
//        // Arrange
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .eventId(eventId)
//                .name("Event")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .coverImageKey(null)
//                .galleryImageKeys(Set.of(
//                        "events/1/gallery/img1.jpg",
//                        "events/1/gallery/img2.jpg",
//                        "events/1/gallery/img3.jpg"
//                ))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
//        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
//        when(repository.deleteEvent(eventId)).thenReturn(true);
//
//        // Act
//        useCase.execute(eventId);
//
//        // Assert
//        verify(imageUrlsCache).invalidate("events/1/gallery/img1.jpg");
//        verify(imageUrlsCache).invalidate("events/1/gallery/img2.jpg");
//        verify(imageUrlsCache).invalidate("events/1/gallery/img3.jpg");
//        verify(imageUrlsCache, times(3)).invalidate(anyString());
//    }

    @Test
    void shouldNotInvalidateCache_whenNoImages() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Event")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .coverImageKey(null)
                .galleryImageKeys(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
        when(repository.deleteEvent(eventId)).thenReturn(true);

        // Act
        useCase.execute(eventId);

        // Assert
        verifyNoInteractions(imageUrlsCache);
    }

//    @Test
//    void shouldContinueDatabaseDeletion_whenR2DeletionFails() {
//        // Arrange
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .eventId(eventId)
//                .name("Event")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .coverImageKey("events/1/cover/img.jpg")
//                .galleryImageKeys(null)
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
//        when(httpRepository.deleteEventFolder(eventId)).thenReturn(false); // R2 deletion fails
//        when(repository.deleteEvent(eventId)).thenReturn(true);
//
//        // Act
//        DeleteEventResponse response = useCase.execute(eventId);
//
//        // Assert
//        assertFalse(response.imagesDeleted());
//        verify(httpRepository).deleteEventFolder(eventId);
//        verifyNoInteractions(imageUrlsCache); // Cache not invalidated when R2 delete fails
//        verify(repository).deleteEvent(eventId); // Database deletion still proceeds
//    }

    @Test
    void shouldSkipR2Deletion_whenR2Disabled() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Event")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .coverImageKey("events/1/cover/img.jpg")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(repository.deleteEvent(eventId)).thenReturn(true);

        // Act - httpRepository is mocked but use case checks for null
        DeleteEventResponse response = useCase.execute(eventId);

        // Assert
        assertFalse(response.imagesDeleted()); // Since httpRepository will be null in real scenario
        verify(repository).deleteEvent(eventId);
    }

    @Test
    void shouldThrowException_whenDatabaseDeletionFails() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Event")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(repository.deleteEvent(eventId)).thenReturn(false); // Database deletion fails

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(eventId));

        assertEquals("Failed to delete event from database", exception.getMessage());
        verify(repository).deleteEvent(eventId);
    }

    @Test
    void shouldHandleEmptyImageKeys() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Event")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .coverImageKey("")
                .galleryImageKeys(Set.of())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
        when(repository.deleteEvent(eventId)).thenReturn(true);

        // Act
        useCase.execute(eventId);

        // Assert
        verifyNoInteractions(imageUrlsCache); // Empty keys should not trigger cache invalidation
    }

//    @Test
//    void shouldDeleteInCorrectOrder() {
//        // Arrange - Verify deletion order: 1) Fetch, 2) R2 delete, 3) Cache invalidate, 4) DB delete
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .eventId(eventId)
//                .name("Event")
//                .eventDate(LocalDate.now())
//                .startTime(LocalTime.now())
//                .endTime(LocalTime.now())
//                .coverImageKey("events/1/cover/img.jpg")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
//        when(httpRepository.deleteEventFolder(eventId)).thenReturn(true);
//        when(repository.deleteEvent(eventId)).thenReturn(true);
//
//        // Act
//        useCase.execute(eventId);
//
//        // Assert - Verify order
//        var inOrder = inOrder(repository, httpRepository, imageUrlsCache);
//        inOrder.verify(repository).findEventById(eventId);
//        inOrder.verify(httpRepository).deleteEventFolder(eventId);
//        inOrder.verify(imageUrlsCache).invalidate("events/1/cover/img.jpg");
//        inOrder.verify(repository).deleteEvent(eventId);
//    }

    @Test
    void shouldReturnSuccessResponse_withCorrectEventName() {
        // Arrange
        Long eventId = 1L;
        Event event = Event.builder()
                .eventId(eventId)
                .name("Annual Wesak Celebration")
                .eventDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(repository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(repository.deleteEvent(eventId)).thenReturn(true);

        // Act
        DeleteEventResponse response = useCase.execute(eventId);

        // Assert
        assertTrue(response.message().contains("Annual Wesak Celebration"));
        assertTrue(response.message().contains(String.valueOf(eventId)));
        assertTrue(response.message().contains("successfully deleted"));
    }

    @Test
    void shouldReturnNotFoundResponse_withCorrectEventId() {
        // Arrange
        Long eventId = 12345L;
        when(repository.findEventById(eventId)).thenReturn(Optional.empty());

        // Act
        DeleteEventResponse response = useCase.execute(eventId);

        // Assert
        assertTrue(response.message().contains("12345"));
        assertTrue(response.message().contains("not found"));
        assertEquals(12345L, response.eventId());
    }
}
