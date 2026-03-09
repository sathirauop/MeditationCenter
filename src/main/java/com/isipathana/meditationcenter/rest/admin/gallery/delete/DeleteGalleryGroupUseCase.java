package com.isipathana.meditationcenter.rest.admin.gallery.delete;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for deleting a gallery group and all its photos.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteGalleryGroupUseCase {

    private final DeleteGalleryGroupDataAccess repository;

    @Transactional
    public void execute(Long groupId) {
        log.info("Deleting gallery group: {}", groupId);

        if (!repository.groupExists(groupId)) {
            throw new ResourceNotFoundException("Gallery group not found: " + groupId);
        }

        // Photos are deleted via CASCADE in the database
        repository.deleteGroup(groupId);
        log.info("Gallery group deleted: {}", groupId);
    }
}
