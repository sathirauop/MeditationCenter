package com.isipathana.meditationcenter.rest.admin.override.delete;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * UseCase for deleting an override.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteOverrideUseCase {

    private final DeleteOverrideDataAccess repository;

    @Transactional
    public DeleteOverrideResponse execute(Long overrideId) {
        log.info("Deleting override ID: {}", overrideId);

        ScheduleOverride override = repository.findOverrideById(overrideId)
                .orElseThrow(() -> new ResourceNotFoundException("Override not found"));

        repository.deleteOverride(overrideId);

        log.info("Successfully deleted override for date: {}", override.overrideDate());

        return new DeleteOverrideResponse(
                true,
                String.format("Override for date '%s' (ID: %d) deleted successfully",
                        override.overrideDate(), overrideId),
                overrideId,
                override.overrideDate()
        );
    }

    @Transactional
    public DeleteOverrideResponse executeByDate(LocalDate date) {
        log.info("Deleting override for date: {}", date);

        ScheduleOverride override = repository.findOverrideByDate(date)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No override found for date: %s", date)));

        repository.deleteOverrideByDate(date);

        log.info("Successfully deleted override for date: {} (ID: {})", date, override.overrideId());

        return new DeleteOverrideResponse(
                true,
                String.format("Override for date '%s' (ID: %d) deleted successfully",
                        date, override.overrideId()),
                override.overrideId(),
                date
        );
    }
}
