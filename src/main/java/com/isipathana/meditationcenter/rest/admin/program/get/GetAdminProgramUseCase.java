package com.isipathana.meditationcenter.rest.admin.program.get;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for getting a meditation program by ID (admin).
 * Handles business logic for retrieving program details with presigned URLs.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAdminProgramUseCase {

    private final GetAdminProgramDataAccess repository;
    private final GetAdminProgramResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    public GetAdminProgramResponse execute(Long programId) {
        log.info("Fetching meditation program with ID: {}", programId);

        MeditationProgram program = repository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Meditation program not found with ID: " + programId));

        log.info("Program found: {}", program.name());

        return responseBuilder.build(program);
    }
}
