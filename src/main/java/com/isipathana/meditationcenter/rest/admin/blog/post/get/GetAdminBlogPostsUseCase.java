package com.isipathana.meditationcenter.rest.admin.blog.post.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * UseCase for fetching all blog posts (admin view).
 * Includes drafts and all statuses.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAdminBlogPostsUseCase {

    private final GetAdminBlogPostsDataAccess repository;
    private final GetAdminBlogPostsResponseBuilder presenter;

    /**
     * Executes the use case to fetch all blog posts.
     *
     * @param request Filter and pagination parameters
     * @return Paginated response with blog posts
     */
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetAdminBlogPostsResponse> execute(GetAdminBlogPostsRequest request) {
        log.info("Fetching all blog posts (admin): limit={}, offset={}, status={}, authorId={}",
                request.limit(), request.offset(), request.status(), request.authorId());

        // Get total count for pagination
        long totalCount = repository.countAllBlogPosts(request);
        log.debug("Total posts matching filters: {}", totalCount);

        // Fetch posts
        Stream<BlogPostWithTags> posts = repository.getAllBlogPosts(request);

        // Build response
        return presenter.build(posts, request.offset(), totalCount);
    }
}
