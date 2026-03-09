package com.isipathana.meditationcenter.rest.admin.gallery.patch;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

/**
 * jOOQ repository for updating gallery groups.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchGalleryGroupRepository implements PatchGalleryGroupDataAccess {

    private final DSLContext dsl;

    @Override
    public boolean updateGroup(Long groupId, PatchGalleryGroupRequest request) {
        var update = dsl.update(table("gallery_groups"));
        UpdateSetMoreStep<?> setStep = null;

        if (request.name() != null) {
            setStep = (setStep == null ? update.set(field("name"), request.name())
                    : setStep.set(field("name"), request.name()));
        }
        if (request.nameSi() != null) {
            setStep = (setStep == null ? update.set(field("name_si"), request.nameSi())
                    : setStep.set(field("name_si"), request.nameSi()));
        }
        if (request.sortOrder() != null) {
            setStep = (setStep == null ? update.set(field("sort_order"), request.sortOrder())
                    : setStep.set(field("sort_order"), request.sortOrder()));
        }
        if (request.active() != null) {
            setStep = (setStep == null ? update.set(field("active"), request.active())
                    : setStep.set(field("active"), request.active()));
        }

        if (setStep == null) {
            // Nothing to update
            return true;
        }

        // Always update the updated_at timestamp
        setStep = setStep.set(field("updated_at"), LocalDateTime.now());

        int rows = setStep
                .where(field("group_id").eq(groupId))
                .execute();

        return rows > 0;
    }
}
