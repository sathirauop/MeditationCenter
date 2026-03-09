package com.isipathana.meditationcenter.rest.admin.gallery.get;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository implementation for fetching gallery groups.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetGalleryGroupsRepository implements GetGalleryGroupsDataAccess {

    private final DSLContext dsl;

    @Override
    public List<GetGalleryGroupsResponse> getAllGroups() {
        var photoCount = DSL.selectCount()
                .from(table("gallery_photos"))
                .where(field("gallery_photos.group_id").eq(field("gallery_groups.group_id")))
                .asField("photo_count");

        return dsl.select(
                field("group_id", Long.class),
                field("name", String.class),
                field("name_si", String.class),
                field("sort_order", Integer.class),
                field("active", Boolean.class),
                photoCount,
                field("created_at", LocalDateTime.class),
                field("updated_at", LocalDateTime.class))
                .from(table("gallery_groups"))
                .orderBy(field("sort_order").asc(), field("created_at").desc())
                .fetch(r -> GetGalleryGroupsResponse.builder()
                        .groupId(r.get("group_id", Long.class))
                        .name(r.get("name", String.class))
                        .nameSi(r.get("name_si", String.class))
                        .sortOrder(r.get("sort_order", Integer.class))
                        .active(r.get("active", Boolean.class))
                        .photoCount(r.get("photo_count", Integer.class))
                        .createdAt(r.get("created_at", LocalDateTime.class))
                        .updatedAt(r.get("updated_at", LocalDateTime.class))
                        .build());
    }
}
