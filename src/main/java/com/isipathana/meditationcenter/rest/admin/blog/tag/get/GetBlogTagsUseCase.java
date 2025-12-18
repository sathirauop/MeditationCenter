package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for retrieving all blog tags.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetBlogTagsUseCase {

    private final GetBlogTagsDataAccess repository;
    private final GetBlogTagsResponseBuilder presenter;

    /**
     * Retrieves all blog tags with post counts.
     *
     * @return List of blog tag responses
     */
    @Transactional(readOnly = true)
    public List<GetBlogTagsResponse> execute() {
        log.info("Retrieving all blog tags");

        var tagsWithCount = repository.findAllTagsWithCount();

        return presenter.build(tagsWithCount);
    }
}
