package com.isipathana.meditationcenter.rest.admin.program.post;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.MEDITATION_PROGRAM;

/**
 * Repository implementation for creating meditation programs using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostProgramRepository implements PostProgramDataAccess {

    private final DSLContext dslContext;

    @Override
    public MeditationProgram createProgram(MeditationProgram program) {
        var record = dslContext
                .insertInto(MEDITATION_PROGRAM)
                .set(MEDITATION_PROGRAM.NAME, program.name())
                .set(MEDITATION_PROGRAM.DESCRIPTION, program.description())
                .set(MEDITATION_PROGRAM.NAME_SI, program.nameSi())
                .set(MEDITATION_PROGRAM.DESCRIPTION_SI, program.descriptionSi())
                .set(MEDITATION_PROGRAM.MAX_SEATS, program.maxSeats())
                .set(MEDITATION_PROGRAM.COVER_IMAGE_KEY, program.coverImageKey())
                .set(MEDITATION_PROGRAM.GALLERY_IMAGE_KEYS, program.galleryImageKeys() != null
                        ? program.galleryImageKeys().toArray(new String[0])
                        : null)
                .set(MEDITATION_PROGRAM.IS_ACTIVE, program.isActive() != null ? program.isActive() : true)
                .returning(MEDITATION_PROGRAM.fields())
                .fetchOne();

        if (record == null) {
            throw new IllegalStateException("Failed to create meditation program");
        }

        return MeditationProgram.builder()
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
                .build();
    }

    @Override
    public MeditationProgram updateImageKeys(Long programId, String coverImageKey, Set<String> galleryImageKeys) {
        int updatedRows = dslContext.update(MEDITATION_PROGRAM)
                .set(MEDITATION_PROGRAM.COVER_IMAGE_KEY, coverImageKey)
                .set(MEDITATION_PROGRAM.GALLERY_IMAGE_KEYS, galleryImageKeys != null && !galleryImageKeys.isEmpty()
                        ? galleryImageKeys.toArray(new String[0])
                        : null)
                .where(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID.eq(programId))
                .execute();

        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Meditation program not found with ID: " + programId);
        }

        var record = dslContext
                .selectFrom(MEDITATION_PROGRAM)
                .where(MEDITATION_PROGRAM.MEDITATION_PROGRAM_ID.eq(programId))
                .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("Meditation program not found with ID: " + programId);
        }

        return MeditationProgram.builder()
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
                .build();
    }
}
