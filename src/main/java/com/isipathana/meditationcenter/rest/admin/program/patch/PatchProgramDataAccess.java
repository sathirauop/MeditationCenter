package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Optional;
import java.util.Set;

/**
 * Data access interface for updating meditation programs.
 *
 * @author Sathira Basnayake
 */
public interface PatchProgramDataAccess {

    /**
     * Finds a meditation program by ID.
     *
     * @param programId The program ID
     * @return The program if found
     */
    Optional<MeditationProgram> findById(Long programId);

    /**
     * Updates a meditation program with partial data.
     * Only non-null fields will be updated.
     *
     * @param program The program with fields to update
     * @return The updated program
     */
    MeditationProgram updateProgram(MeditationProgram program);

    /**
     * Updates the image keys for a meditation program.
     *
     * @param programId        The program ID
     * @param coverImageKey    The new cover image key (null to keep existing)
     * @param galleryImageKeys The new gallery image keys (null to keep existing)
     * @return The updated program
     */
    MeditationProgram updateImageKeys(Long programId, String coverImageKey, Set<String> galleryImageKeys);
}
