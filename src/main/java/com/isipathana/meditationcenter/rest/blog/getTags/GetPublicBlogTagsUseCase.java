package com.isipathana.meditationcenter.rest.blog.getTags;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

/**
 * UseCase for fetching public blog tags.
 * Returns tags with published post counts only (excludes drafts).
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetPublicBlogTagsUseCase {

    private final GetPublicBlogTagsDataAccess repository;
    private final GetPublicBlogTagsResponseBuilder presenter;

    /**
     * Executes the use case to fetch all blog tags with published post counts.
     *
     * @return List of tags with post counts
     */
    @Transactional(readOnly = true)
    public List<GetPublicBlogTagsResponse> execute() {
        log.info("Fetching public blog tags with published post counts");

        Stream<PublicBlogTagWithCount> tags = repository.findAllTagsWithPublishedCount();

        return presenter.build(tags);
    }
}
