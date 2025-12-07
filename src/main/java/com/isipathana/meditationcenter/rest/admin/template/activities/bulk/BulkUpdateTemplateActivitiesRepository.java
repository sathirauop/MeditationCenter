package com.isipathana.meditationcenter.rest.admin.template.activities.bulk;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for bulk updating template activities.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BulkUpdateTemplateActivitiesRepository implements BulkUpdateTemplateActivitiesDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<ScheduleTemplate> findTemplateById(Long templateId) {
        log.debug("Finding template by ID: {}", templateId);

        return dslContext.selectFrom(SCHEDULE_TEMPLATE)
                .where(SCHEDULE_TEMPLATE.TEMPLATE_ID.eq(templateId))
                .fetchOptional()
                .map(record -> ScheduleTemplate.builder()
                        .templateId(record.getTemplateId())
                        .name(record.getName())
                        .description(record.getDescription())
                        .isActive(record.getIsActive())
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
    public int deleteAllTemplateActivities(Long templateId) {
        log.debug("Deleting all activities for template {}", templateId);

        return dslContext.deleteFrom(TEMPLATE_SCHEDULE_ACTIVITY)
                .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
                .execute();
    }

    @Override
    public TemplateScheduleActivity createTemplateActivity(TemplateScheduleActivity templateActivity) {
        log.debug("Creating template activity for template {}, activity {}",
                templateActivity.templateId(), templateActivity.activityId());

        var record = dslContext.insertInto(TEMPLATE_SCHEDULE_ACTIVITY)
                .set(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID, templateActivity.templateId())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.ACTIVITY_ID, templateActivity.activityId())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME, templateActivity.startTime())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.END_TIME, templateActivity.endTime())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.NOTES, templateActivity.notes())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.CREATED_AT, LocalDateTime.now())
                .set(TEMPLATE_SCHEDULE_ACTIVITY.UPDATED_AT, LocalDateTime.now())
                .returning()
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to create template activity");
        }

        return TemplateScheduleActivity.builder()
                .id(record.getId())
                .templateId(record.getTemplateId())
                .activityId(record.getActivityId())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
