package com.isipathana.meditationcenter.rest.admin.gallery.getSingle;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for fetching a single gallery group with all its photos.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetGalleryGroupUseCase {

    private final GetGalleryGroupDataAccess repository;

    @Transactional(readOnly = true)
    public GetGalleryGroupResponse execute(Long groupId) {
        log.info("Fetching gallery group: {}", groupId);

        GalleryGroup group = repository.getGroupById(groupId);
        if (group == null) {
            throw new ResourceNotFoundException("Gallery group not found: " + groupId);
        }

        List<GalleryPhoto> photos = repository.getPhotosByGroupId(groupId);

        return GetGalleryGroupResponse.builder()
                .groupId(group.groupId())
                .name(group.name())
                .nameSi(group.nameSi())
                .sortOrder(group.sortOrder())
                .active(group.active())
                .photos(photos.stream().map(p -> GetGalleryGroupResponse.PhotoResponse.builder()
                        .photoId(p.photoId())
                        .imageKey(p.imageKey())
                        .caption(p.caption())
                        .captionSi(p.captionSi())
                        .sortOrder(p.sortOrder())
                        .createdAt(p.createdAt())
                        .build()).toList())
                .createdAt(group.createdAt())
                .updatedAt(group.updatedAt())
                .build();
    }
}
