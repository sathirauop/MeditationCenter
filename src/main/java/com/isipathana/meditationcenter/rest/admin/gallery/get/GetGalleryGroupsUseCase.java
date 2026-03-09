package com.isipathana.meditationcenter.rest.admin.gallery.get;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for fetching all gallery groups (admin view).
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetGalleryGroupsUseCase {

    private final GetGalleryGroupsDataAccess repository;

    @Transactional(readOnly = true)
    public List<GetGalleryGroupsResponse> execute() {
        log.info("Fetching all gallery groups (admin)");
        return repository.getAllGroups();
    }
}
