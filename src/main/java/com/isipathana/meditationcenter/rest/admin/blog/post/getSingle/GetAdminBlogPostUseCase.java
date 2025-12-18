package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.blog.BlogPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for fetching a single blog post for admin editing.
 * Returns post regardless of status (includes drafts).
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAdminBlogPostUseCase {

    private final GetAdminBlogPostDataAccess repository;
    private final GetAdminBlogPostResponseBuilder presenter;

    /**
     * Executes the use case to fetch a blog post by ID.
     *
     * @param postId The post ID
     * @return Response DTO with full post details
     * @throws ResourceNotFoundException if post not found or deleted
     */
    @Transactional(readOnly = true)
    public GetAdminBlogPostResponse execute(Long postId) {
        log.info("Fetching blog post for editing: {}", postId);

        BlogPost post = repository.getBlogPostById(postId)
                .orElseThrow(() -> {
                    log.warn("Blog post not found: {}", postId);
                    return new ResourceNotFoundException("Blog post not found: " + postId);
                });

        return presenter.build(post);
    }
}
