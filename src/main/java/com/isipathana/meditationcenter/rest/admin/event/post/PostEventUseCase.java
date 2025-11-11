package com.isipathana.meditationcenter.rest.admin.event.post;

import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * UseCase for creating a new event.
 * Handles business logic for event creation by admin users.
 * <p>
 * Supports optional image uploads to R2 storage when r2.enabled=true.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class PostEventUseCase {

    private final PostEventDataAccess repository;

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private PostEventHttpDataAccess httpRepository;

    /**
     * Execute event creation with optional image uploads.
     *
     * @param request       Event creation request
     * @param coverImage    Optional cover image file
     * @param galleryImages Optional list of gallery image files
     * @return Response with created event details
     * @throws ValidationException if end time is before start time
     */
    @Transactional
    public PostEventResponse execute(
            PostEventRequest request,
            MultipartFile coverImage,
            List<MultipartFile> galleryImages
    ) {
        // Validate time range
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ValidationException("End time must be after start time");
        }

        // First, create the event to get the event ID (needed for image upload paths)
        Event eventToCreate = Event.builder()
                .name(request.name())
                .description(request.description())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .location(request.location())
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();

        Event createdEvent = repository.createEvent(eventToCreate);

        // Upload images if R2 is enabled and images are provided
        String coverImageKey = null;
        Set<String> galleryImageKeys = new HashSet<>();

        if (httpRepository != null) {
            // Upload cover image if provided
            if (coverImage != null && !coverImage.isEmpty()) {
                coverImageKey = httpRepository.uploadCoverImage(createdEvent.eventId(), coverImage);
            }

            // Upload gallery images if provided
            if (galleryImages != null && !galleryImages.isEmpty()) {
                List<String> uploadedKeys = httpRepository.uploadGalleryImages(
                        createdEvent.eventId(),
                        galleryImages
                );
                galleryImageKeys.addAll(uploadedKeys);
            }

            // Update event record in database with image keys if any were uploaded
            if (coverImageKey != null || !galleryImageKeys.isEmpty()) {
                createdEvent = repository.updateImageKeys(
                        createdEvent.eventId(),
                        coverImageKey,
                        galleryImageKeys.isEmpty() ? null : galleryImageKeys
                );
            }
        }

        // Map to response
        return new PostEventResponse(
                createdEvent.eventId(),
                createdEvent.name(),
                createdEvent.description(),
                createdEvent.eventDate(),
                createdEvent.startTime(),
                createdEvent.endTime(),
                createdEvent.location(),
                createdEvent.coverImageKey(),
                createdEvent.galleryImageKeys(),
                createdEvent.isActive(),
                createdEvent.createdAt(),
                createdEvent.updatedAt()
        );
    }

    /**
     * Execute event creation without images (backward compatibility).
     *
     * @param request Event creation request
     * @return Response with created event details
     */
    @Transactional
    public PostEventResponse execute(PostEventRequest request) {
        return execute(request, null, null);
    }
}
