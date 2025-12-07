package com.isipathana.meditationcenter.rest.admin.template.activities.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for deleting template activities.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DeleteTemplateActivityRepository implements DeleteTemplateActivityDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<TemplateScheduleActivity> findTemplateActivityById(Long templateActivityId) {
        log.debug("Finding template activity by ID: {}", templateActivityId);

        return dslContext.selectFrom(TEMPLATE_SCHEDULE_ACTIVITY)
                .where(TEMPLATE_SCHEDULE_ACTIVITY.ID.eq(templateActivityId))
                .fetchOptional()
                .map(record -> TemplateScheduleActivity.builder()
                        .id(record.getId())
                        .templateId(record.getTemplateId())
                        .activityId(record.getActivityId())
                        .startTime(record.getStartTime())
                        .endTime(record.getEndTime())
                        .notes(record.getNotes())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public Optional<Activity> findActivityById(Long activityId) {
        log.debug("Finding activity by ID: {}", activityId);

        return dslContext.selectFrom(ACTIVITY)
                .where(ACTIVITY.ACTIVITY_ID.eq(activityId))
                .fetchOptional()
                .map(record -> Activity.builder()
                        .activityId(record.getActivityId())
                        .title(record.getTitle())
                        .description(record.getDescription())
                        .mediaUrl(record.getMediaUrl())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public boolean deleteTemplateActivity(Long templateActivityId) {
        log.debug("Deleting template activity: {}", templateActivityId);

        int deletedRows = dslContext.deleteFrom(TEMPLATE_SCHEDULE_ACTIVITY)
                .where(TEMPLATE_SCHEDULE_ACTIVITY.ID.eq(templateActivityId))
                .execute();

        return deletedRows > 0;
    }
}
