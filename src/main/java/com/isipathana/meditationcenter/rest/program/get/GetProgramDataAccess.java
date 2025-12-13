package com.isipathana.meditationcenter.rest.program.get;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Optional;

/**
 * Data access interface for getting meditation programs (public).
 *
 * @author Sathira Basnayake
 */
public interface GetProgramDataAccess {

    /**
     * Finds an active meditation program by ID.
     * Only returns programs where isActive = true.
     *
     * @param programId The program ID
     * @return The program if found and active
     */
    Optional<MeditationProgram> findActiveById(Long programId);
}
