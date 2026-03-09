package com.isipathana.meditationcenter.rest.admin.gallery.patch;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating a gallery group.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchGalleryGroupUseCase {

    private final PatchGalleryGroupDataAccess repository;

    @Transactional
    public void execute(Long groupId, PatchGalleryGroupRequest request) {
        log.info("Updating gallery group: {}", groupId);

        boolean updated = repository.updateGroup(groupId, request);
        if (!updated) {
            throw new ResourceNotFoundException("Gallery group not found: " + groupId);
        }

        log.info("Gallery group updated: {}", groupId);
    }
}
