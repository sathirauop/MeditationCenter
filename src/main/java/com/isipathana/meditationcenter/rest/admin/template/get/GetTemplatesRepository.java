package com.isipathana.meditationcenter.rest.admin.template.get;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static com.isipathana.meditationcenter.jooq.Tables.*;
import static org.jooq.impl.DSL.count;

/**
 * Repository implementation for retrieving templates.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class GetTemplatesRepository implements GetTemplatesDataAccess {

    private final DSLContext dslContext;

    @Override
    public Stream<ScheduleTemplate> getTemplates(int limit, int offset) {
        log.debug("Fetching templates with limit {} and offset {}", limit, offset);

        return dslContext.selectFrom(SCHEDULE_TEMPLATE)
                .orderBy(SCHEDULE_TEMPLATE.CREATED_AT.desc())
                .limit(limit)
                .offset(offset)
                .fetchStream()
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
    public long countTemplates() {
        log.debug("Counting total templates");

        Long count = dslContext.selectCount()
                .from(SCHEDULE_TEMPLATE)
                .fetchOne(0, Long.class);

        return count != null ? count : 0L;
    }

    @Override
    public int getActivityCount(Long templateId) {
        log.debug("Counting activities for template {}", templateId);

        Integer count = dslContext.select(count())
                .from(TEMPLATE_SCHEDULE_ACTIVITY)
                .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
                .fetchOne(0, Integer.class);

        return count != null ? count : 0;
    }
}
