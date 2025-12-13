package com.isipathana.meditationcenter.rest.program.getActive;

import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.MEDITATION_PROGRAM;

/**
 * Repository implementation for getting the active meditation program (public).
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetActiveProgramRepository implements GetActiveProgramDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<MeditationProgram> findActiveProgram() {
        var record = dslContext
                .selectFrom(MEDITATION_PROGRAM)
                .where(MEDITATION_PROGRAM.IS_ACTIVE.eq(true))
                .orderBy(MEDITATION_PROGRAM.CREATED_AT.desc())
                .limit(1)
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(MeditationProgram.builder()
                .meditationProgramId(record.get(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID))
                .name(record.get(MEDITATION_PROGRAM.NAME))
                .description(record.get(MEDITATION_PROGRAM.DESCRIPTION))
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
}
