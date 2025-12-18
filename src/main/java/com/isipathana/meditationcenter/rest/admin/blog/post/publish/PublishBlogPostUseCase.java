package com.isipathana.meditationcenter.rest.admin.blog.post.publish;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.exception.ValidationException;
import com.isipathana.meditationcenter.records.blog.BlogPost;
import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POSTS;

/**
 * UseCase for publishing blog posts.
 * Changes status from DRAFT to PUBLISHED and sets published_at timestamp.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublishBlogPostUseCase {

    private final DSLContext dslContext;

    @Transactional
    public void execute(Long postId) {
        log.info("Publishing blog post ID: {}", postId);

        // Fetch post
        var record = dslContext
                .selectFrom(BLOG_POSTS)
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .and(BLOG_POSTS.DELETED_AT.isNull())
                .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Blog post not found with ID: " + postId);
        }

        // Validate
        if (record.getStatus() == BlogPostStatus.PUBLISHED) {
            throw new ValidationException("Post is already published");
        }

        if (record.getTitle() == null || record.getContent() == null) {
            throw new ValidationException("Cannot publish incomplete post");
        }

        // Publish
        int updated = dslContext
                .update(BLOG_POSTS)
                .set(BLOG_POSTS.STATUS, BlogPostStatus.PUBLISHED)
                .set(BLOG_POSTS.PUBLISHED_AT, LocalDateTime.now())
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .execute();

        if (updated == 0) {
            throw new RuntimeException("Failed to publish post");
        }

        log.info("Blog post published successfully: {}", postId);
    }
}
