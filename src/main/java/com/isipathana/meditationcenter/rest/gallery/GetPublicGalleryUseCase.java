package com.isipathana.meditationcenter.rest.gallery;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UseCase for fetching the public gallery.
 * Returns only active groups with their photos.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetPublicGalleryUseCase {

    private final GetPublicGalleryDataAccess repository;

    @Transactional(readOnly = true)
    public List<GetPublicGalleryResponse> execute() {
        log.info("Fetching public gallery");

        List<GalleryGroup> groups = repository.getActiveGroups();
        if (groups.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groups.stream().map(GalleryGroup::groupId).toList();
        List<GalleryPhoto> allPhotos = repository.getPhotosByGroupIds(groupIds);

        // Group photos by group ID
        Map<Long, List<GalleryPhoto>> photosByGroup = allPhotos.stream()
                .collect(Collectors.groupingBy(GalleryPhoto::groupId));

        return groups.stream().map(group -> GetPublicGalleryResponse.builder()
                .groupId(group.groupId())
                .name(group.name())
                .nameSi(group.nameSi())
                .photos(photosByGroup.getOrDefault(group.groupId(), List.of()).stream()
                        .map(p -> GetPublicGalleryResponse.PhotoResponse.builder()
                                .photoId(p.photoId())
                                .imageKey(p.imageKey())
                                .caption(p.caption())
                                .captionSi(p.captionSi())
                                .build())
                        .toList())
                .build()).toList();
    }
}
