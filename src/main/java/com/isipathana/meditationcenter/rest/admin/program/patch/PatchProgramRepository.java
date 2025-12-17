package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.MEDITATION_PROGRAM;

/**
 * Repository implementation for updating meditation programs using jOOQ.
 * Supports partial updates - only non-null fields are updated.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchProgramRepository implements PatchProgramDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<MeditationProgram> findById(Long programId) {
        var record = dslContext
                .selectFrom(MEDITATION_PROGRAM)
                .where(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID.eq(programId))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(MeditationProgram.builder()
                .meditationProgramId(record.get(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID))
                .name(record.get(MEDITATION_PROGRAM.NAME))
                .description(record.get(MEDITATION_PROGRAM.DESCRIPTION))
                .nameSi(record.get(MEDITATION_PROGRAM.NAME_SI))
                .descriptionSi(record.get(MEDITATION_PROGRAM.DESCRIPTION_SI))
                .maxSeats(record.get(MEDITATION_PROGRAM.MAX_SEATS))
                .coverImageKey(record.get(MEDITATION_PROGRAM.COVER_IMAGE_KEY))
                .galleryImageKeys(record.get(MEDITATION_PROGRAM.GALLERY_IMAGE_KEYS) != null
                        ? Set.of(record.get(MEDITATION_PROGRAM.GALLERY_IMAGE_KEYS))
                        : null)
                .isActive(record.get(MEDITATION_PROGRAM.IS_ACTIVE))
                .createdAt(record.get(MEDITATION_PROGRAM.CREATED_AT))
                .updatedAt(record.get(MEDITATION_PROGRAM.UPDATED_AT))
                .build());
    }

    @Override
    public MeditationProgram updateProgram(MeditationProgram program) {
        var updateStep = dslContext.update(MEDITATION_PROGRAM);
        UpdateSetMoreStep<?> query = null;

        // Build dynamic query - only update non-null fields
        if (program.name() != null) {
            query = updateStep.set(MEDITATION_PROGRAM.NAME, program.name());
        }

        if (program.description() != null) {
            query = query != null
                    ? query.set(MEDITATION_PROGRAM.DESCRIPTION, program.description())
                    : updateStep.set(MEDITATION_PROGRAM.DESCRIPTION, program.description());
        }

        if (program.nameSi() != null) {
            query = query != null
                    ? query.set(MEDITATION_PROGRAM.NAME_SI, program.nameSi())
                    : updateStep.set(MEDITATION_PROGRAM.NAME_SI, program.nameSi());
        }

        if (program.descriptionSi() != null) {
            query = query != null
                    ? query.set(MEDITATION_PROGRAM.DESCRIPTION_SI, program.descriptionSi())
                    : updateStep.set(MEDITATION_PROGRAM.DESCRIPTION_SI, program.descriptionSi());
        }

        if (program.maxSeats() != null) {
            query = query != null
                    ? query.set(MEDITATION_PROGRAM.MAX_SEATS, program.maxSeats())
                    : updateStep.set(MEDITATION_PROGRAM.MAX_SEATS, program.maxSeats());
        }

        if (program.isActive() != null) {
            query = query != null
                    ? query.set(MEDITATION_PROGRAM.IS_ACTIVE, program.isActive())
                    : updateStep.set(MEDITATION_PROGRAM.IS_ACTIVE, program.isActive());

            // BUSINESS LOGIC: If activating this program, deactivate all other programs
            // Only one program can be active at a time
            if (program.isActive()) {
                dslContext.update(MEDITATION_PROGRAM)
                        .set(MEDITATION_PROGRAM.IS_ACTIVE, false)
                        .set(MEDITATION_PROGRAM.UPDATED_AT, DSL.currentLocalDateTime())
                        .where(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID.ne(program.meditationProgramId()))
                        .execute();
            }
        }

        // Always update updated_at timestamp
        if (query != null) {
            query = query.set(MEDITATION_PROGRAM.UPDATED_AT, DSL.currentLocalDateTime());
        }

        // Execute update if any fields were set
        if (query != null) {
            query.where(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID.eq(program.meditationProgramId())).execute();
        }

        // Fetch and return updated program
        return findById(program.meditationProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Meditation program not found with ID: " + program.meditationProgramId()));
    }
}
