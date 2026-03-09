package com.isipathana.meditationcenter.rest.admin.gallery.post;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for creating gallery groups.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostGalleryGroupRepository implements PostGalleryGroupDataAccess {

    private final DSLContext dsl;

    @Override
    public GalleryGroup createGroup(GalleryGroup group) {
        return dsl.insertInto(table("gallery_groups"))
                .set(field("name"), group.name())
                .set(field("name_si"), group.nameSi())
                .set(field("sort_order"), group.sortOrder())
                .set(field("active"), group.active())
                .returning(
                        field("group_id"),
                        field("name"),
                        field("name_si"),
                        field("sort_order"),
                        field("active"),
                        field("created_at"),
                        field("updated_at"))
                .fetchOne(r -> GalleryGroup.builder()
                        .groupId(r.get("group_id", Long.class))
                        .name(r.get("name", String.class))
                        .nameSi(r.get("name_si", String.class))
                        .sortOrder(r.get("sort_order", Integer.class))
                        .active(r.get("active", Boolean.class))
                        .createdAt(r.get("created_at", LocalDateTime.class))
                        .updatedAt(r.get("updated_at", LocalDateTime.class))
                        .build());
    }
}
