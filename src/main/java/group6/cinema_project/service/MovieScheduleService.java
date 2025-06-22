package group6.cinema_project.service;

import org.springframework.stereotype.Service;

import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.exception.ScheduleConflictException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface MovieScheduleService {
        Optional<ScreeningScheduleDto> getScreeningScheduleById(Integer id);

        ScreeningScheduleDto saveOrUpdateScreeningSchedule(ScreeningScheduleDto screeningScheduleDto);

        void deleteScreeningSchedule(Integer id);

        List<ScreeningScheduleDto> getAllScreeningSchedules();

        List<ScreeningScheduleDto> getFilteredScreeningSchedules(String searchTerm, String filterBy);

        /**
         * Get all screening schedules with related entity data for display
         */
        List<ScreeningScheduleDto> getAllScreeningSchedulesForDisplay();

        /**
         * Get filtered screening schedules with related entity data for display
         */
        List<ScreeningScheduleDto> getFilteredScreeningSchedulesForDisplay(
                        Integer movieId, LocalDate screeningDate, Integer screeningRoomId);

        /**
         * Validate that a screening schedule does not conflict with existing schedules
         * in the same screening room on the same date.
         *
         * @param screeningScheduleDto The schedule to validate
         * @throws ScheduleConflictException if there are conflicting schedules
         */
        void validateScheduleConflicts(ScreeningScheduleDto screeningScheduleDto) throws ScheduleConflictException;

        /**
         * Save or update a screening schedule with conflict validation
         *
         * @param screeningScheduleDto The schedule to save
         * @return The saved schedule
         * @throws ScheduleConflictException if there are conflicting schedules
         */
        ScreeningScheduleDto saveOrUpdateScreeningScheduleWithValidation(ScreeningScheduleDto screeningScheduleDto)
                        throws ScheduleConflictException;
}
