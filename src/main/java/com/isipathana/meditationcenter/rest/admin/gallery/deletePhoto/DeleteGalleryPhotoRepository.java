package com.isipathana.meditationcenter.rest.admin.gallery.deletePhoto;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for deleting gallery photos.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteGalleryPhotoRepository implements DeleteGalleryPhotoDataAccess {

    private final DSLContext dsl;

    @Override
    public boolean photoExistsInGroup(Long groupId, Long photoId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(table("gallery_photos"))
                        .where(field("photo_id").eq(photoId))
                        .and(field("group_id").eq(groupId)));
    }

    @Override
    public void deletePhoto(Long photoId) {
        dsl.deleteFrom(table("gallery_photos"))
                .where(field("photo_id").eq(photoId))
                .execute();
    }
}
