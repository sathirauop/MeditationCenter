package com.isipathana.meditationcenter.rest.schedule.get;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for getting schedule.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetScheduleRepository implements GetScheduleDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<ScheduleOverride> findOverrideByDate(LocalDate date) {
        return dslContext.selectFrom(SCHEDULE_OVERRIDE)
                .where(SCHEDULE_OVERRIDE.OVERRIDE_DATE.eq(date))
                .fetchOptional(record -> ScheduleOverride.builder()
                        .overrideId(record.getOverrideId())
                        .overrideDate(record.getOverrideDate())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public List<GetScheduleResponse.ScheduleActivity> getOverrideActivities(Long overrideId) {
        return dslContext
                .select(
                        ACTIVITY.ACTIVITY_ID,
                        ACTIVITY.TITLE,
                        ACTIVITY.DESCRIPTION,
                        OVERRIDE_ACTIVITY.START_TIME,
                        OVERRIDE_ACTIVITY.END_TIME,
                        OVERRIDE_ACTIVITY.NOTES
                )
                .from(OVERRIDE_ACTIVITY)
                .join(ACTIVITY).on(OVERRIDE_ACTIVITY.ACTIVITY_ID.eq(ACTIVITY.ACTIVITY_ID))
                .where(OVERRIDE_ACTIVITY.OVERRIDE_ID.eq(overrideId)
                        .and(OVERRIDE_ACTIVITY.IS_CANCELLED.isFalse()))
                .orderBy(OVERRIDE_ACTIVITY.START_TIME.asc())
                .fetch(record -> new GetScheduleResponse.ScheduleActivity(
                        record.get(ACTIVITY.ACTIVITY_ID),
                        record.get(ACTIVITY.TITLE),
                        record.get(ACTIVITY.DESCRIPTION),
                        record.get(OVERRIDE_ACTIVITY.START_TIME).toString(),
                        record.get(OVERRIDE_ACTIVITY.END_TIME).toString(),
                        record.get(OVERRIDE_ACTIVITY.NOTES)
                ));
    }

    @Override
    public Optional<ScheduleTemplate> findActiveTemplate() {
        return dslContext.selectFrom(SCHEDULE_TEMPLATE)
                .where(SCHEDULE_TEMPLATE.IS_ACTIVE.isTrue())
                .fetchOptional(record -> ScheduleTemplate.builder()
                        .templateId(record.getTemplateId())
                        .name(record.getName())
                        .description(record.getDescription())
                        .isActive(record.getIsActive())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public List<GetScheduleResponse.ScheduleActivity> getTemplateActivities(Long templateId) {
        return dslContext
                .select(
                        ACTIVITY.ACTIVITY_ID,
                        ACTIVITY.TITLE,
                        ACTIVITY.DESCRIPTION,
                        TEMPLATE_SCHEDULE_ACTIVITY.START_TIME,
                        TEMPLATE_SCHEDULE_ACTIVITY.END_TIME,
                        TEMPLATE_SCHEDULE_ACTIVITY.NOTES
                )
                .from(TEMPLATE_SCHEDULE_ACTIVITY)
                .join(ACTIVITY).on(TEMPLATE_SCHEDULE_ACTIVITY.ACTIVITY_ID.eq(ACTIVITY.ACTIVITY_ID))
                .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
                .orderBy(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME.asc())
                .fetch(record -> new GetScheduleResponse.ScheduleActivity(
                        record.get(ACTIVITY.ACTIVITY_ID),
                        record.get(ACTIVITY.TITLE),
                        record.get(ACTIVITY.DESCRIPTION),
                        record.get(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME).toString(),
                        record.get(TEMPLATE_SCHEDULE_ACTIVITY.END_TIME).toString(),
                        record.get(TEMPLATE_SCHEDULE_ACTIVITY.NOTES)
                ));
    }
}
