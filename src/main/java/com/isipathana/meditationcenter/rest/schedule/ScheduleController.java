package com.isipathana.meditationcenter.rest.schedule;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.schedule.get.GetScheduleRequest;
import com.isipathana.meditationcenter.rest.schedule.get.GetScheduleResponse;
import com.isipathana.meditationcenter.rest.schedule.get.GetScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for public schedule endpoints.
 * Allows anyone to view the daily meditation schedule.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Schedule.BASE)
@RequiredArgsConstructor
public class ScheduleController {

    private final GetScheduleUseCase getScheduleUseCase;

    /**
     * Get today's schedule.
     * Public endpoint - no authentication required.
     *
     * @return today's schedule (override or template)
     */
    @GetMapping(EndPoints.Schedule.GET_TODAY)
    public ResponseEntity<GetScheduleResponse> getTodaySchedule() {
        GetScheduleRequest request = new GetScheduleRequest(LocalDate.now());
        GetScheduleResponse response = getScheduleUseCase.handle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get schedule for a specific date.
     * Public endpoint - no authentication required.
     *
     * @param date the date to get schedule for (format: yyyy-MM-dd)
     * @return schedule for the specified date (override or template)
     */
    @GetMapping(EndPoints.Schedule.GET_BY_DATE)
    public ResponseEntity<GetScheduleResponse> getScheduleByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GetScheduleRequest request = new GetScheduleRequest(date);
        GetScheduleResponse response = getScheduleUseCase.handle(request);
        return ResponseEntity.ok(response);
    }
}
