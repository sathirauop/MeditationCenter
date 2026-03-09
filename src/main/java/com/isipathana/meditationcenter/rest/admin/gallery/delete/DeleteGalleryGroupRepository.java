package com.isipathana.meditationcenter.rest.admin.gallery.delete;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for deleting gallery groups.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteGalleryGroupRepository implements DeleteGalleryGroupDataAccess {

    private final DSLContext dsl;

    @Override
    public boolean groupExists(Long groupId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(table("gallery_groups"))
                        .where(field("group_id").eq(groupId)));
    }

    @Override
    public void deleteGroup(Long groupId) {
        dsl.deleteFrom(table("gallery_groups"))
                .where(field("group_id").eq(groupId))
                .execute();
    }
}
