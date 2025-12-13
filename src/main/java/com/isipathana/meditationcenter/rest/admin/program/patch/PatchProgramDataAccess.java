package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Optional;

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
}
