package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.isipathana.meditationcenter.jooq.tables.records.BlogTagsRecord;
import com.isipathana.meditationcenter.records.blog.BlogTag;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_TAGS;

/**
 * Repository implementation for creating blog tags.
 * Uses jOOQ for type-safe database access.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostBlogTagRepository implements PostBlogTagDataAccess {

    private final DSLContext dslContext;

    @Override
    public BlogTag createTag(BlogTag tag) {
        BlogTagsRecord record = dslContext
                .insertInto(BLOG_TAGS)
                .set(BLOG_TAGS.NAME, tag.name())
                .set(BLOG_TAGS.NAME_SI, tag.nameSi())
                .set(BLOG_TAGS.SLUG, tag.slug())
                .returning()
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to create blog tag");
        }

        return mapToBlogTag(record);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return dslContext
                .fetchExists(
                        dslContext.selectFrom(BLOG_TAGS)
                                .where(BLOG_TAGS.SLUG.eq(slug))
                );
    }

    @Override
    public boolean existsByName(String name) {
        return dslContext
                .fetchExists(
                        dslContext.selectFrom(BLOG_TAGS)
                                .where(BLOG_TAGS.NAME.equalIgnoreCase(name))
                );
    }

    /**
     * Maps a jOOQ BlogTagsRecord to a domain BlogTag.
     */
    private BlogTag mapToBlogTag(BlogTagsRecord record) {
        return BlogTag.builder()
                .tagId(record.getTagId())
                .name(record.getName())
                .nameSi(record.getNameSi())
                .slug(record.getSlug())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
