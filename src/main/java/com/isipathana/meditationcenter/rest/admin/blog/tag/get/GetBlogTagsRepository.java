package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import com.isipathana.meditationcenter.records.blog.BlogTag;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_POST_TAGS;
import static com.isipathana.meditationcenter.jooq.Tables.BLOG_TAGS;
import static org.jooq.impl.DSL.count;

/**
 * Repository implementation for retrieving blog tags.
 * Uses jOOQ for type-safe database access with post count aggregation.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetBlogTagsRepository implements GetBlogTagsDataAccess {

    private final DSLContext dslContext;

    @Override
    public Stream<BlogTagWithCount> findAllTagsWithCount() {
        var postCountField = count(BLOG_POST_TAGS.POST_ID).as("post_count");

        return dslContext
                .select(
                        BLOG_TAGS.TAG_ID,
                        BLOG_TAGS.NAME,
                        BLOG_TAGS.NAME_SI,
                        BLOG_TAGS.SLUG,
                        BLOG_TAGS.CREATED_AT,
                        postCountField
                )
                .from(BLOG_TAGS)
                .leftJoin(BLOG_POST_TAGS).on(BLOG_TAGS.TAG_ID.eq(BLOG_POST_TAGS.TAG_ID))
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

                    Long postCount = record.get(postCountField, Long.class);

                    return new BlogTagWithCount(tag, postCount != null ? postCount : 0L);
                });
    }
}
