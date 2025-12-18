package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.jooq.tables.records.BlogPostsRecord;
import com.isipathana.meditationcenter.records.blog.BlogPost;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POSTS;
import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POST_TAGS;

/**
 * Repository for creating blog posts.
 * Handles blog post creation, image key updates, and tag associations.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostBlogPostRepository implements PostBlogPostDataAccess {

    private final DSLContext dslContext;

    @Override
    public BlogPost createBlogPost(BlogPost post) {
        BlogPostsRecord record = dslContext
                .insertInto(BLOG_POSTS)
                .set(BLOG_POSTS.TITLE, post.title())
                .set(BLOG_POSTS.EXCERPT, post.excerpt())
                .set(BLOG_POSTS.CONTENT, post.content())
                .set(BLOG_POSTS.TITLE_SI, post.titleSi())
                .set(BLOG_POSTS.EXCERPT_SI, post.excerptSi())
                .set(BLOG_POSTS.CONTENT_SI, post.contentSi())
                .set(BLOG_POSTS.SLUG, post.slug())
                .set(BLOG_POSTS.AUTHOR_ID, post.authorId())
                .set(BLOG_POSTS.STATUS, post.status())
                .set(BLOG_POSTS.META_TITLE, post.metaTitle())
                .set(BLOG_POSTS.META_DESCRIPTION, post.metaDescription())
                .set(BLOG_POSTS.VIEW_COUNT, 0L)
                .set(BLOG_POSTS.VERSION, 1L)
                .returning()
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to create blog post");
        }

        return mapToBlogPost(record);
    }

    @Override
    public BlogPost updateImageKeys(Long postId, String coverImageKey, Set<String> galleryImageKeys) {
        // Build UPDATE statement - must handle both fields properly
        int updated;

        if (coverImageKey != null && galleryImageKeys != null && !galleryImageKeys.isEmpty()) {
            // Both cover and gallery images
            String[] imageKeysArray = galleryImageKeys.toArray(new String[0]);
            updated = dslContext.update(BLOG_POSTS)
                    .set(BLOG_POSTS.COVER_IMAGE_KEY, coverImageKey)
                    .set(BLOG_POSTS.IMAGE_KEYS, imageKeysArray)
                    .where(BLOG_POSTS.POST_ID.eq(postId))
                    .execute();
        } else if (coverImageKey != null) {
            // Only cover image
            updated = dslContext.update(BLOG_POSTS)
                    .set(BLOG_POSTS.COVER_IMAGE_KEY, coverImageKey)
                    .where(BLOG_POSTS.POST_ID.eq(postId))
                    .execute();
        } else if (galleryImageKeys != null && !galleryImageKeys.isEmpty()) {
            // Only gallery images
            String[] imageKeysArray = galleryImageKeys.toArray(new String[0]);
            updated = dslContext.update(BLOG_POSTS)
                    .set(BLOG_POSTS.IMAGE_KEYS, imageKeysArray)
                    .where(BLOG_POSTS.POST_ID.eq(postId))
                    .execute();
        } else {
            // Nothing to update - just fetch and return
            BlogPostsRecord record = dslContext
                    .selectFrom(BLOG_POSTS)
                    .where(BLOG_POSTS.POST_ID.eq(postId))
                    .fetchOne();
            if (record == null) {
                throw new RuntimeException("Post not found: " + postId);
            }
            return mapToBlogPost(record);
        }

        if (updated == 0) {
            throw new RuntimeException("Failed to update image keys");
        }

        // Fetch updated record
        BlogPostsRecord record = dslContext
                .selectFrom(BLOG_POSTS)
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Post not found after image update");
        }

        return mapToBlogPost(record);
    }

    @Override
    public void associateTags(Long postId, Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        var insertStep = dslContext.insertInto(BLOG_POST_TAGS, BLOG_POST_TAGS.POST_ID, BLOG_POST_TAGS.TAG_ID);

        for (Long tagId : tagIds) {
            insertStep = insertStep.values(postId, tagId);
        }

        insertStep.execute();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return dslContext
                .fetchExists(
                        dslContext.selectFrom(BLOG_POSTS)
                                .where(BLOG_POSTS.SLUG.eq(slug))
                );
    }

    /**
     * Maps jOOQ BlogPostsRecord to domain BlogPost.
     */
    private BlogPost mapToBlogPost(BlogPostsRecord record) {
        // Convert TEXT[] to Set<String>
        Set<String> imageKeys = new HashSet<>();
        String[] imageKeysArray = record.getImageKeys();
        if (imageKeysArray != null) {
            imageKeys = Set.of(imageKeysArray);
        }

        return BlogPost.builder()
                .postId(record.getPostId())
                .title(record.getTitle())
                .excerpt(record.getExcerpt())
                .content(record.getContent())
                .titleSi(record.getTitleSi())
                .excerptSi(record.getExcerptSi())
                .contentSi(record.getContentSi())
                .slug(record.getSlug())
                .authorId(record.getAuthorId())
                .coverImageKey(record.getCoverImageKey())
                .imageKeys(imageKeys)
                .status(record.getStatus())
                .publishedAt(record.getPublishedAt())
                .metaTitle(record.getMetaTitle())
                .metaDescription(record.getMetaDescription())
                .viewCount(record.getViewCount())
                .version(record.getVersion())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .deletedAt(record.getDeletedAt())
                .build();
    }
}
