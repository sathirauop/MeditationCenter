package com.isipathana.meditationcenter.rest.admin.override.delete;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for deleting overrides.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteOverrideRepository implements DeleteOverrideDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<ScheduleOverride> findOverrideById(Long overrideId) {
        return dslContext.selectFrom(SCHEDULE_OVERRIDE)
                .where(SCHEDULE_OVERRIDE.OVERRIDE_ID.eq(overrideId))
                .fetchOptional(record -> ScheduleOverride.builder()
                        .overrideId(record.getOverrideId())
                        .overrideDate(record.getOverrideDate())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public void deleteOverride(Long overrideId) {
        // Activities will be cascade deleted by database
        dslContext.deleteFrom(SCHEDULE_OVERRIDE)
                .where(SCHEDULE_OVERRIDE.OVERRIDE_ID.eq(overrideId))
                .execute();
    }
}
