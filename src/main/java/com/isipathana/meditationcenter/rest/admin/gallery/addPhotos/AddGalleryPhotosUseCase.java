package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UseCase for adding photos to a gallery group.
 * Handles R2 upload (if enabled) and database record creation.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddGalleryPhotosUseCase {

    private final AddGalleryPhotosDataAccess repository;

    @Autowired(required = false)
    private AddGalleryPhotosHttpDataAccess httpRepository;

    @Transactional
    public AddGalleryPhotosResponse execute(Long groupId, List<MultipartFile> photos) {
        log.info("Adding {} photos to gallery group: {}", photos.size(), groupId);

        if (!repository.groupExists(groupId)) {
            throw new ResourceNotFoundException("Gallery group not found: " + groupId);
        }

        int currentMaxSortOrder = repository.getMaxSortOrder(groupId);
        List<String> imageKeys;

        // Upload to R2 if enabled
        if (httpRepository != null) {
            imageKeys = httpRepository.uploadPhotos(groupId, photos);
        } else {
            // R2 disabled - generate placeholder keys
            imageKeys = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                String key = String.format("gallery/%d/%s.jpg", groupId, UUID.randomUUID());
                imageKeys.add(key);
            }
        }

        // Create database records
        List<AddGalleryPhotosResponse.PhotoResponse> createdPhotos = new ArrayList<>();
        for (int i = 0; i < imageKeys.size(); i++) {
            GalleryPhoto photoToCreate = GalleryPhoto.builder()
                    .groupId(groupId)
                    .imageKey(imageKeys.get(i))
                    .sortOrder(currentMaxSortOrder + i + 1)
                    .build();

            GalleryPhoto created = repository.createPhoto(photoToCreate);
            createdPhotos.add(AddGalleryPhotosResponse.PhotoResponse.builder()
                    .photoId(created.photoId())
                    .imageKey(created.imageKey())
                    .sortOrder(created.sortOrder())
                    .createdAt(created.createdAt())
                    .build());
        }

        log.info("Added {} photos to gallery group {}", createdPhotos.size(), groupId);

        return AddGalleryPhotosResponse.builder()
                .groupId(groupId)
                .photosAdded(createdPhotos.size())
                .photos(createdPhotos)
                .build();
    }
}
