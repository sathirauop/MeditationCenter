package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating meditation programs.
 * Handles business logic for partial program updates.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchProgramUseCase {

    private final PatchProgramDataAccess repository;
    private final PatchProgramResponseBuilder responseBuilder;

    @Transactional
    public PatchProgramResponse execute(Long programId, PatchProgramRequest request) {
        log.info("Updating meditation program with ID: {}", programId);

        // Verify program exists
        MeditationProgram existingProgram = repository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Meditation program not found with ID: " + programId));

        log.info("Existing program found: {}", existingProgram.name());

        // Build program with only non-null fields from request
        MeditationProgram programToUpdate = MeditationProgram.builder()
                .meditationProgramId(programId)
                .name(request.name())
                .description(request.description())
                .nameSi(request.nameSi())
                .descriptionSi(request.descriptionSi())
                .maxSeats(request.maxSeats())
                .isActive(request.isActive())
                .build();

        // Update program
        MeditationProgram updatedProgram = repository.updateProgram(programToUpdate);

        log.info("Program updated successfully: {}", updatedProgram.name());

        // Build and return response using presenter
        return responseBuilder.build(updatedProgram);
    }
}
