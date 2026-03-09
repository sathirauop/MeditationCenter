package com.isipathana.meditationcenter.rest.admin.gallery.deletePhoto;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for deleting a single photo from a gallery group.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteGalleryPhotoUseCase {

    private final DeleteGalleryPhotoDataAccess repository;

    @Transactional
    public void execute(Long groupId, Long photoId) {
        log.info("Deleting photo {} from gallery group {}", photoId, groupId);

        if (!repository.photoExistsInGroup(groupId, photoId)) {
            throw new ResourceNotFoundException(
                    String.format("Photo %d not found in gallery group %d", photoId, groupId));
        }

        repository.deletePhoto(photoId);
        log.info("Photo {} deleted from gallery group {}", photoId, groupId);
    }
}
