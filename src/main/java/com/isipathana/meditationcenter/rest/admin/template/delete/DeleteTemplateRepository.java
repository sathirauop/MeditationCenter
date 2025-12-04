package com.isipathana.meditationcenter.rest.admin.template.delete;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for template deletion operations.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DeleteTemplateRepository implements DeleteTemplateDataAccess {

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
    public boolean deleteTemplate(Long templateId) {
        log.debug("Deleting template: {}", templateId);

        int deletedRows = dslContext.deleteFrom(SCHEDULE_TEMPLATE)
                .where(SCHEDULE_TEMPLATE.TEMPLATE_ID.eq(templateId))
                .execute();

        return deletedRows > 0;
    }
}
