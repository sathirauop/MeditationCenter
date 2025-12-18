package com.isipathana.meditationcenter.rest.blog.getTags;

import com.isipathana.meditationcenter.records.blog.BlogPostStatus;
import com.isipathana.meditationcenter.records.blog.BlogTag;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static com.isipathana.meditationcenter.jooq.Tables.*;
import static org.jooq.impl.DSL.count;

/**
 * Repository implementation for retrieving public blog tags.
 * Only counts PUBLISHED posts (excludes drafts and deleted posts).
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetPublicBlogTagsRepository implements GetPublicBlogTagsDataAccess {

    private final DSLContext dslContext;

    @Override
    public Stream<PublicBlogTagWithCount> findAllTagsWithPublishedCount() {
        var publishedPostCountField = count(BLOG_POST_TAGS.POST_ID).filterWhere(
                BLOG_POSTS.STATUS.eq(BlogPostStatus.PUBLISHED)
                        .and(BLOG_POSTS.DELETED_AT.isNull())
        ).as("published_post_count");

        return dslContext
                .select(
                        BLOG_TAGS.TAG_ID,
                        BLOG_TAGS.NAME,
                        BLOG_TAGS.NAME_SI,
                        BLOG_TAGS.SLUG,
                        BLOG_TAGS.CREATED_AT,
                        publishedPostCountField
                )
                .from(BLOG_TAGS)
                .leftJoin(BLOG_POST_TAGS).on(BLOG_TAGS.TAG_ID.eq(BLOG_POST_TAGS.TAG_ID))
                .leftJoin(BLOG_POSTS).on(BLOG_POST_TAGS.POST_ID.eq(BLOG_POSTS.POST_ID))
                .groupBy(
                        BLOG_TAGS.TAG_ID,
                        BLOG_TAGS.NAME,
                        BLOG_TAGS.NAME_SI,
                        BLOG_TAGS.SLUG,
                        BLOG_TAGS.CREATED_AT
                )
                .orderBy(BLOG_TAGS.NAME.asc())
                .fetchStream()
                .map(record -> {
                    var tag = BlogTag.builder()
                            .tagId(record.get(BLOG_TAGS.TAG_ID))
                            .name(record.get(BLOG_TAGS.NAME))
                            .nameSi(record.get(BLOG_TAGS.NAME_SI))
                            .slug(record.get(BLOG_TAGS.SLUG))
                            .createdAt(record.get(BLOG_TAGS.CREATED_AT))
                            .build();

                    Long postCount = record.get(publishedPostCountField, Long.class);

                    return new PublicBlogTagWithCount(tag, postCount != null ? postCount : 0L);
                });
    }
}
