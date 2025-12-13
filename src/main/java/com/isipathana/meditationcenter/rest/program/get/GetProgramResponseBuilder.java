package com.isipathana.meditationcenter.rest.program.get;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

/**
 * Response builder interface for transforming MeditationProgram to GetProgramResponse (public).
 *
 * @author Sathira Basnayake
 */
public interface GetProgramResponseBuilder {

    /**
     * Builds a public response from a meditation program.
     *
     * @param program The meditation program
     * @return The response with presigned URLs
     */
    GetProgramResponse build(MeditationProgram program);
}
