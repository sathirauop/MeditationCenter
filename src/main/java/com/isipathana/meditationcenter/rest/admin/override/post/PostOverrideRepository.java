package com.isipathana.meditationcenter.rest.admin.override.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for creating schedule overrides.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostOverrideRepository implements PostOverrideDataAccess {

    private final DSLContext dslContext;

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
    public boolean overrideExistsForDate(LocalDate date) {
        return dslContext.fetchExists(
                dslContext.selectFrom(SCHEDULE_OVERRIDE)
                        .where(SCHEDULE_OVERRIDE.OVERRIDE_DATE.eq(date))
        );
    }

    @Override
    public ScheduleOverride createOverride(ScheduleOverride override) {
        var record = dslContext.insertInto(SCHEDULE_OVERRIDE)
                .set(SCHEDULE_OVERRIDE.OVERRIDE_DATE, override.overrideDate())
                .returningResult(SCHEDULE_OVERRIDE.fields())
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to create override");
        }

        return ScheduleOverride.builder()
                .overrideId(record.get(SCHEDULE_OVERRIDE.OVERRIDE_ID))
                .overrideDate(record.get(SCHEDULE_OVERRIDE.OVERRIDE_DATE))
                .createdAt(record.get(SCHEDULE_OVERRIDE.CREATED_AT))
                .updatedAt(record.get(SCHEDULE_OVERRIDE.UPDATED_AT))
                .build();
    }

    @Override
    public OverrideActivity createOverrideActivity(OverrideActivity overrideActivity) {
        var record = dslContext.insertInto(OVERRIDE_ACTIVITY)
                .set(OVERRIDE_ACTIVITY.OVERRIDE_ID, overrideActivity.overrideId())
                .set(OVERRIDE_ACTIVITY.ACTIVITY_ID, overrideActivity.activityId())
                .set(OVERRIDE_ACTIVITY.START_TIME, overrideActivity.startTime())
                .set(OVERRIDE_ACTIVITY.END_TIME, overrideActivity.endTime())
                .set(OVERRIDE_ACTIVITY.NOTES, overrideActivity.notes())
                .returningResult(OVERRIDE_ACTIVITY.fields())
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to create override activity");
        }

        return OverrideActivity.builder()
                .id(record.get(OVERRIDE_ACTIVITY.ID))
                .overrideId(record.get(OVERRIDE_ACTIVITY.OVERRIDE_ID))
                .activityId(record.get(OVERRIDE_ACTIVITY.ACTIVITY_ID))
                .startTime(record.get(OVERRIDE_ACTIVITY.START_TIME))
                .endTime(record.get(OVERRIDE_ACTIVITY.END_TIME))
                .notes(record.get(OVERRIDE_ACTIVITY.NOTES))
                .createdAt(record.get(OVERRIDE_ACTIVITY.CREATED_AT))
                .build();
    }

    @Override
    public List<Activity> findActivitiesByIds(List<Long> activityIds) {
        return dslContext.selectFrom(ACTIVITY)
                .where(ACTIVITY.ACTIVITY_ID.in(activityIds))
                .fetch(record -> Activity.builder()
                        .activityId(record.getActivityId())
                        .title(record.getTitle())
                        .description(record.getDescription())
                        .mediaUrl(record.getMediaUrl())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }
}
