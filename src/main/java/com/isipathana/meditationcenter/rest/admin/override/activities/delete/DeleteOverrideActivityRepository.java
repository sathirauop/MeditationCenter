package com.isipathana.meditationcenter.rest.admin.override.activities.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for deleting override activities.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteOverrideActivityRepository implements DeleteOverrideActivityDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<OverrideActivity> findOverrideActivityById(Long overrideId, Long overrideActivityId) {
        return dslContext.selectFrom(OVERRIDE_ACTIVITY)
                .where(OVERRIDE_ACTIVITY.OVERRIDE_ID.eq(overrideId)
                        .and(OVERRIDE_ACTIVITY.ID.eq(overrideActivityId)))
                .fetchOptional(record -> OverrideActivity.builder()
                        .id(record.getId())
                        .overrideId(record.getOverrideId())
                        .activityId(record.getActivityId())
                        .startTime(record.getStartTime())
                        .endTime(record.getEndTime())
                        .notes(record.getNotes())
                        .createdAt(record.getCreatedAt())
                        .build());
    }

    @Override
    public Optional<Activity> findActivityById(Long activityId) {
        return dslContext.selectFrom(ACTIVITY)
                .where(ACTIVITY.ACTIVITY_ID.eq(activityId))
                .fetchOptional(record -> Activity.builder()
                        .activityId(record.getActivityId())
                        .title(record.getTitle())
                        .description(record.getDescription())
                        .mediaUrl(record.getMediaUrl())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public void deleteOverrideActivity(Long overrideActivityId) {
        dslContext.deleteFrom(OVERRIDE_ACTIVITY)
                .where(OVERRIDE_ACTIVITY.ID.eq(overrideActivityId))
                .execute();
    }
}
