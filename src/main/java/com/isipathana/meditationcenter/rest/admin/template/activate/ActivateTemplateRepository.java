package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for template activation operations.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ActivateTemplateRepository implements ActivateTemplateDataAccess {

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
    public Optional<ScheduleTemplate> findActiveTemplate() {
        log.debug("Finding active template");

        return dslContext.selectFrom(SCHEDULE_TEMPLATE)
                .where(SCHEDULE_TEMPLATE.IS_ACTIVE.eq(true))
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
    public int deactivateAllTemplates() {
        log.debug("Deactivating all templates");

        return dslContext.update(SCHEDULE_TEMPLATE)
                .set(SCHEDULE_TEMPLATE.IS_ACTIVE, false)
                .set(SCHEDULE_TEMPLATE.UPDATED_AT, LocalDateTime.now())
                .where(SCHEDULE_TEMPLATE.IS_ACTIVE.eq(true))
                .execute();
    }

    @Override
    public ScheduleTemplate activateTemplate(Long templateId) {
        log.debug("Activating template: {}", templateId);

        var record = dslContext.update(SCHEDULE_TEMPLATE)
                .set(SCHEDULE_TEMPLATE.IS_ACTIVE, true)
                .set(SCHEDULE_TEMPLATE.UPDATED_AT, LocalDateTime.now())
                .where(SCHEDULE_TEMPLATE.TEMPLATE_ID.eq(templateId))
                .returning()
                .fetchOne();

        if (record == null) {
            throw new RuntimeException("Failed to activate template");
        }

        return ScheduleTemplate.builder()
                .templateId(record.getTemplateId())
                .name(record.getName())
                .description(record.getDescription())
                .isActive(record.getIsActive())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
