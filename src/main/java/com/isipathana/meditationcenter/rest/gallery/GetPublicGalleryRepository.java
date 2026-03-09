package com.isipathana.meditationcenter.rest.gallery;

import com.isipathana.meditationcenter.records.gallery.GalleryGroup;
import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for public gallery data access.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetPublicGalleryRepository implements GetPublicGalleryDataAccess {

    private final DSLContext dsl;

    @Override
    public List<GalleryGroup> getActiveGroups() {
        return dsl.select(
                field("group_id", Long.class),
                field("name", String.class),
                field("name_si", String.class),
                field("sort_order", Integer.class),
                field("active", Boolean.class),
                field("created_at", LocalDateTime.class),
                field("updated_at", LocalDateTime.class))
                .from(table("gallery_groups"))
                .where(field("active").eq(true))
                .orderBy(field("sort_order").asc(), field("created_at").desc())
                .fetch(r -> GalleryGroup.builder()
                        .groupId(r.get("group_id", Long.class))
                        .name(r.get("name", String.class))
                        .nameSi(r.get("name_si", String.class))
                        .sortOrder(r.get("sort_order", Integer.class))
                        .active(r.get("active", Boolean.class))
                        .createdAt(r.get("created_at", LocalDateTime.class))
                        .updatedAt(r.get("updated_at", LocalDateTime.class))
                        .build());
    }

    @Override
    public List<GalleryPhoto> getPhotosByGroupIds(List<Long> groupIds) {
        return dsl.select(
                field("photo_id", Long.class),
                field("group_id", Long.class),
                field("image_key", String.class),
                field("caption", String.class),
                field("caption_si", String.class),
                field("sort_order", Integer.class),
                field("created_at", LocalDateTime.class))
                .from(table("gallery_photos"))
                .where(field("group_id").in(groupIds))
                .orderBy(field("group_id").asc(), field("sort_order").asc(), field("created_at").asc())
                .fetch(r -> GalleryPhoto.builder()
                        .photoId(r.get("photo_id", Long.class))
                        .groupId(r.get("group_id", Long.class))
                        .imageKey(r.get("image_key", String.class))
                        .caption(r.get("caption", String.class))
                        .captionSi(r.get("caption_si", String.class))
                        .sortOrder(r.get("sort_order", Integer.class))
                        .createdAt(r.get("created_at", LocalDateTime.class))
                        .build());
    }
}
