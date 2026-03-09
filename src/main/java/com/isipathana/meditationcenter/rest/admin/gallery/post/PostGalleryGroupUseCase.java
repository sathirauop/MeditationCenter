package com.isipathana.meditationcenter.rest.admin.gallery.post;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating a gallery group.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostGalleryGroupUseCase {

    private final PostGalleryGroupDataAccess repository;

    @Transactional
    public PostGalleryGroupResponse execute(PostGalleryGroupRequest request) {
        log.info("Creating new gallery group: {}", request.name());

        GalleryGroup groupToCreate = GalleryGroup.builder()
                .name(request.name())
                .nameSi(request.nameSi())
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .active(true)
                .build();

        GalleryGroup created = repository.createGroup(groupToCreate);
        log.info("Gallery group created with ID: {}", created.groupId());

        return PostGalleryGroupResponse.builder()
                .groupId(created.groupId())
                .name(created.name())
                .nameSi(created.nameSi())
                .sortOrder(created.sortOrder())
                .active(created.active())
                .createdAt(created.createdAt())
                .build();
    }
}
