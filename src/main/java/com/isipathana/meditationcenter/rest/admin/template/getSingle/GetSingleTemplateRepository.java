package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class GetSingleTemplateRepository implements GetSingleTemplateDataAccess {

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
    public List<TemplateScheduleActivity> getTemplateActivities(Long templateId) {
        log.debug("Getting activities for template {}", templateId);

        return dslContext.selectFrom(TEMPLATE_SCHEDULE_ACTIVITY)
                .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
                .orderBy(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME.asc())
                .fetch()
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
}
