package com.isipathana.meditationcenter.rest.admin.blog.post.unpublish;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POSTS;

/**
 * UseCase for unpublishing blog posts.
 * Changes status from PUBLISHED back to DRAFT.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnpublishBlogPostUseCase {

    private final DSLContext dslContext;

    @Transactional
    public void execute(Long postId) {
        log.info("Unpublishing blog post ID: {}", postId);

        boolean exists = dslContext
                .fetchExists(
                        dslContext.selectFrom(BLOG_POSTS)
                                .where(BLOG_POSTS.POST_ID.eq(postId))
                                .and(BLOG_POSTS.DELETED_AT.isNull())
                );

        if (!exists) {
            throw new ResourceNotFoundException("Blog post not found with ID: " + postId);
        }

        int updated = dslContext
                .update(BLOG_POSTS)
                .set(BLOG_POSTS.STATUS, BlogPostStatus.DRAFT)
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .execute();

        if (updated == 0) {
            throw new RuntimeException("Failed to unpublish post");
        }

        log.info("Blog post unpublished successfully: {}", postId);
    }
}
