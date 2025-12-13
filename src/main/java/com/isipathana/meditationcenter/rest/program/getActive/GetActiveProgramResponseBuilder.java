package com.isipathana.meditationcenter.rest.program.getActive;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

/**
 * Response builder interface for transforming MeditationProgram to GetActiveProgramResponse (public).
 *
 * @author Sathira Basnayake
 */
public interface GetActiveProgramResponseBuilder {

    /**
     * Builds a public response from the active meditation program.
     *
     * @param program The meditation program
     * @return The response with presigned URLs
     */
    GetActiveProgramResponse build(MeditationProgram program);
}
