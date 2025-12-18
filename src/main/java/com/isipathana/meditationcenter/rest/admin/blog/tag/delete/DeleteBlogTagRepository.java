package com.isipathana.meditationcenter.rest.admin.blog.tag.delete;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.BLOG_TAGS;

/**
 * Repository for deleting blog tags.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteBlogTagRepository implements DeleteBlogTagDataAccess {

    private final DSLContext dslContext;

    @Override
    public boolean existsById(Long tagId) {
        return dslContext
                .fetchExists(
                        dslContext.selectFrom(BLOG_TAGS)
                                .where(BLOG_TAGS.TAG_ID.eq(tagId))
                );
    }

    @Override
    public void deleteTag(Long tagId) {
        dslContext
                .deleteFrom(BLOG_TAGS)
                .where(BLOG_TAGS.TAG_ID.eq(tagId))
                .execute();
    }
}
