package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;
import static org.jooq.impl.DSL.arrayAgg;

/**
 * Repository for fetching a single published blog post by slug.
 * Uses jOOQ for type-safe database queries.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetSingleBlogPostRepository implements GetSingleBlogPostDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<BlogPostWithTags> getPublishedBlogPostBySlug(String slug) {
        Record record = dslContext
                .select(
                        BLOG_POSTS.POST_ID,
                        BLOG_POSTS.TITLE,
                        BLOG_POSTS.EXCERPT,
                        BLOG_POSTS.CONTENT,
                        BLOG_POSTS.TITLE_SI,
                        BLOG_POSTS.EXCERPT_SI,
                        BLOG_POSTS.CONTENT_SI,
                        BLOG_POSTS.SLUG,
                        BLOG_POSTS.AUTHOR_ID,
                        USERS.NAME.as("author_name"),
                        BLOG_POSTS.COVER_IMAGE_KEY,
                        BLOG_POSTS.IMAGE_KEYS,
                        BLOG_POSTS.STATUS,
                        BLOG_POSTS.PUBLISHED_AT,
                        BLOG_POSTS.META_TITLE,
                        BLOG_POSTS.META_DESCRIPTION,
                        BLOG_POSTS.VIEW_COUNT,
                        arrayAgg(BLOG_TAGS.NAME).filterWhere(BLOG_TAGS.NAME.isNotNull()).as("tag_names"),
                        BLOG_POSTS.VERSION,
                        BLOG_POSTS.CREATED_AT,
                        BLOG_POSTS.UPDATED_AT,
                        BLOG_POSTS.DELETED_AT
                )
                .from(BLOG_POSTS)
                .join(USERS).on(BLOG_POSTS.AUTHOR_ID.eq(USERS.USER_ID))
                .leftJoin(BLOG_POST_TAGS).on(BLOG_POSTS.POST_ID.eq(BLOG_POST_TAGS.POST_ID))
                .leftJoin(BLOG_TAGS).on(BLOG_POST_TAGS.TAG_ID.eq(BLOG_TAGS.TAG_ID))
                .where(BLOG_POSTS.SLUG.eq(slug))
                .and(BLOG_POSTS.STATUS.eq(BlogPostStatus.PUBLISHED))
                .and(BLOG_POSTS.DELETED_AT.isNull())
                .groupBy(
                        BLOG_POSTS.POST_ID,
                        BLOG_POSTS.TITLE,
                        BLOG_POSTS.EXCERPT,
                        BLOG_POSTS.CONTENT,
                        BLOG_POSTS.TITLE_SI,
                        BLOG_POSTS.EXCERPT_SI,
                        BLOG_POSTS.CONTENT_SI,
                        BLOG_POSTS.SLUG,
                        BLOG_POSTS.AUTHOR_ID,
                        USERS.NAME,
                        BLOG_POSTS.COVER_IMAGE_KEY,
                        BLOG_POSTS.IMAGE_KEYS,
                        BLOG_POSTS.STATUS,
                        BLOG_POSTS.PUBLISHED_AT,
                        BLOG_POSTS.META_TITLE,
                        BLOG_POSTS.META_DESCRIPTION,
                        BLOG_POSTS.VIEW_COUNT,
                        BLOG_POSTS.VERSION,
                        BLOG_POSTS.CREATED_AT,
                        BLOG_POSTS.UPDATED_AT,
                        BLOG_POSTS.DELETED_AT
                )
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(BlogPostWithTags.builder()
                .postId(record.get(BLOG_POSTS.POST_ID))
                .title(record.get(BLOG_POSTS.TITLE))
                .excerpt(record.get(BLOG_POSTS.EXCERPT))
                .content(record.get(BLOG_POSTS.CONTENT))
                .titleSi(record.get(BLOG_POSTS.TITLE_SI))
                .excerptSi(record.get(BLOG_POSTS.EXCERPT_SI))
                .contentSi(record.get(BLOG_POSTS.CONTENT_SI))
                .slug(record.get(BLOG_POSTS.SLUG))
                .authorId(record.get(BLOG_POSTS.AUTHOR_ID))
                .authorName(record.get("author_name", String.class))
                .coverImageKey(record.get(BLOG_POSTS.COVER_IMAGE_KEY))
                .imageKeys(record.get(BLOG_POSTS.IMAGE_KEYS) != null ?
                        new HashSet<>(Arrays.asList(record.get(BLOG_POSTS.IMAGE_KEYS))) : new HashSet<>())
                .status(record.get(BLOG_POSTS.STATUS))
                .publishedAt(record.get(BLOG_POSTS.PUBLISHED_AT))
                .metaTitle(record.get(BLOG_POSTS.META_TITLE))
                .metaDescription(record.get(BLOG_POSTS.META_DESCRIPTION))
                .viewCount(record.get(BLOG_POSTS.VIEW_COUNT))
                .tagNames(record.get("tag_names", String[].class) != null ?
                        new HashSet<>(Arrays.asList(record.get("tag_names", String[].class))) : new HashSet<>())
                .version(record.get(BLOG_POSTS.VERSION))
                .createdAt(record.get(BLOG_POSTS.CREATED_AT))
                .updatedAt(record.get(BLOG_POSTS.UPDATED_AT))
                .deletedAt(record.get(BLOG_POSTS.DELETED_AT))
                .build());
    }

    @Override
    public void incrementViewCount(Long postId) {
        dslContext
                .update(BLOG_POSTS)
                .set(BLOG_POSTS.VIEW_COUNT, BLOG_POSTS.VIEW_COUNT.plus(1))
                .where(BLOG_POSTS.POST_ID.eq(postId))
                .execute();
    }
}
