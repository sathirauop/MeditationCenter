package com.isipathana.meditationcenter.rest.admin.blog.post.delete;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POSTS;

/**
 * UseCase for soft deleting blog posts.
 * Sets deleted_at timestamp instead of physically deleting.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteBlogPostUseCase {

    private final DSLContext dslContext;

    @Transactional
    public void execute(Long postId) {
        log.info("Soft deleting blog post ID: {}", postId);

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
                .set(BLOG_POSTS.DELETED_AT, LocalDateTime.now())
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .execute();

        if (updated == 0) {
            throw new RuntimeException("Failed to delete post");
        }

        log.info("Blog post soft deleted successfully: {}", postId);
    }
}
