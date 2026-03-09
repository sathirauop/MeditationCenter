package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import com.isipathana.meditationcenter.records.gallery.GalleryPhoto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for adding photos to gallery groups.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class AddGalleryPhotosRepository implements AddGalleryPhotosDataAccess {

    private final DSLContext dsl;

    @Override
    public boolean groupExists(Long groupId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(table("gallery_groups"))
                        .where(field("group_id").eq(groupId)));
    }

    @Override
    public int getMaxSortOrder(Long groupId) {
        Integer max = dsl.select(max(field("sort_order", Integer.class)))
                .from(table("gallery_photos"))
                .where(field("group_id").eq(groupId))
                .fetchOne(0, Integer.class);
        return max != null ? max : 0;
    }

    @Override
    public GalleryPhoto createPhoto(GalleryPhoto photo) {
        return dsl.insertInto(table("gallery_photos"))
                .set(field("group_id"), photo.groupId())
                .set(field("image_key"), photo.imageKey())
                .set(field("caption"), photo.caption())
                .set(field("caption_si"), photo.captionSi())
                .set(field("sort_order"), photo.sortOrder())
                .returning(
                        field("photo_id"),
                        field("group_id"),
                        field("image_key"),
                        field("caption"),
                        field("caption_si"),
                        field("sort_order"),
                        field("created_at"))
                .fetchOne(r -> GalleryPhoto.builder()
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
