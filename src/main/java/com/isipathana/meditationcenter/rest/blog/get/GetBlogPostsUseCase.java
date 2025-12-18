package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * UseCase for fetching published blog posts.
 * Handles business logic for listing published blog posts with filtering and pagination.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetBlogPostsUseCase {

    private final GetBlogPostsDataAccess repository;
    private final GetBlogPostsResponseBuilder presenter;

    /**
     * Executes the use case to fetch published blog posts.
     *
     * @param request Filter and pagination parameters
     * @return Paginated response with blog posts
     */
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetBlogPostsResponse> execute(GetBlogPostsRequest request) {
        log.info("Fetching published blog posts: limit={}, offset={}, tagIds={}, search={}",
                request.limit(), request.offset(), request.tagIds(), request.search());

        // Get total count for pagination
        long totalCount = repository.countPublishedBlogPosts(request);
        log.debug("Total published posts matching filters: {}", totalCount);

        // Fetch posts
        Stream<BlogPostWithTags> posts = repository.getPublishedBlogPosts(request);

        // Build response
        return presenter.build(posts, request.offset(), totalCount);
    }
}
