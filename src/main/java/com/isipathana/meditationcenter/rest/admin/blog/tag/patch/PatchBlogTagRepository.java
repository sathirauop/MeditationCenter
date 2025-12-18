package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import com.isipathana.meditationcenter.jooq.tables.records.BlogTagsRecord;
import com.isipathana.meditationcenter.records.blog.BlogTag;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_TAGS;

/**
 * Repository for updating blog tags.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchBlogTagRepository implements PatchBlogTagDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<BlogTag> findTagById(Long tagId) {
        return dslContext
                .selectFrom(BLOG_TAGS)
                .where(BLOG_TAGS.TAG_ID.eq(tagId))
                .fetchOptional()
                .map(this::mapToBlogTag);
    }

    @Override
    public BlogTag updateTag(BlogTag tag) {
        UpdateSetFirstStep<BlogTagsRecord> updateStep = dslContext.update(BLOG_TAGS);
        UpdateSetMoreStep<?> query = null;

        if (tag.name() != null) {
            query = updateStep.set(BLOG_TAGS.NAME, tag.name());
        }

        if (tag.nameSi() != null) {
            query = query != null
                    ? query.set(BLOG_TAGS.NAME_SI, tag.nameSi())
                    : updateStep.set(BLOG_TAGS.NAME_SI, tag.nameSi());
        }

        if (query == null) {
            throw new IllegalArgumentException("No fields to update");
        }

        int updated = query
                .where(BLOG_TAGS.TAG_ID.eq(tag.tagId()))
                .execute();

        if (updated == 0) {
            throw new RuntimeException("Failed to update tag");
        }

        // Fetch the updated record
        BlogTagsRecord record = dslContext
                .selectFrom(BLOG_TAGS)
                .where(BLOG_TAGS.TAG_ID.eq(tag.tagId()))
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Tag not found after update");
        }

        return mapToBlogTag(record);
    }

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
