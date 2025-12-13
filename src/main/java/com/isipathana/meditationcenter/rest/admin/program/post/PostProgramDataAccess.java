package com.isipathana.meditationcenter.rest.admin.program.post;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Set;

/**
 * Data access interface for creating meditation programs.
 *
 * @author Sathira Basnayake
 */
public interface PostProgramDataAccess {

    /**
     * Creates a new meditation program in the database.
     *
     * @param program The program to create
     * @return The created program with generated ID and timestamps
     */
    MeditationProgram createProgram(MeditationProgram program);

    /**
     * Updates the image keys for an existing program.
     *
     * @param programId The program ID
     * @param coverImageKey The cover image R2 key
     * @param galleryImageKeys The gallery image R2 keys
     * @return The updated program
     */
    MeditationProgram updateImageKeys(Long programId, String coverImageKey, Set<String> galleryImageKeys);
}
