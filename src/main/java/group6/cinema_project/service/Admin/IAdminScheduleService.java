package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleGroupedByDateDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.exception.ScheduleConflictException;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IAdminScheduleService {
        Optional<ScreeningScheduleDto> getScreeningScheduleById(Integer id);

        ScreeningScheduleDto saveOrUpdateScreeningSchedule(ScreeningScheduleDto screeningScheduleDto);

        void deleteScreeningSchedule(Integer id);

        List<ScreeningScheduleDto> getAllScreeningSchedules();

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
         * Get movies grouped by their schedule status for tab navigation
         *
         * @param status The schedule status (ACTIVE, UPCOMING, ENDED)
         * @return List of movies with the specified status
         */
        List<MovieDto> getMoviesByScheduleStatus(String status);

        /**
         * Get detailed schedules for a specific movie
         *
         * @param movieId The movie ID
         * @return List of schedules for the movie
         */
        List<ScreeningScheduleDto> getSchedulesByMovieId(Integer movieId);

        /**
         * Get schedules for a specific movie grouped by date and room
         * This method pre-processes the data to reduce frontend complexity
         *
         * @param movieId The movie ID
         * @return List of schedules grouped by date, then by room
         */
        List<ScheduleGroupedByDateDto> getSchedulesByMovieIdGrouped(Integer movieId);

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

        /**
         * Get movies that are currently playing based on dynamic date/time calculation
         * A movie is currently playing if it has at least one schedule where:
         * - The screening has started (screeningDate + startTime <= currentDateTime)
         * - The screening hasn't ended yet (screeningDate + endTime >= currentDateTime)
         *
         * @return List of currently playing movies
         */
        List<MovieDto> getCurrentlyPlayingMovies();

        /**
         * Get movies that are coming soon based on dynamic date/time calculation
         * A movie is coming soon if it has at least one schedule where:
         * - The screening hasn't started yet (screeningDate + startTime >
         * currentDateTime)
         *
         * @return List of coming soon movies
         */
        List<MovieDto> getComingSoonMovies();

        /**
         * Get movies that have stopped showing based on dynamic date/time calculation
         * A movie has stopped showing if ALL its schedules have:
         * - All screenings have ended (screeningDate + endTime < currentDateTime)
         *
         * @return List of stopped showing movies
         */
        List<MovieDto> getStoppedShowingMovies();

        /**
         * Get movies that have at least one schedule with 'ENDED' status
         * This is more intuitive for users who want to see movies with ended schedules
         *
         * @return List of movies with ended schedules
         */
        List<MovieDto> getMoviesWithEndedSchedules();

        /**
         * Get movies that have at least one schedule with 'ACTIVE' status
         * This is more intuitive for users who want to see movies with active schedules
         *
         * @return List of movies with active schedules
         */
        List<MovieDto> getMoviesWithActiveSchedules();

        /**
         * Get detailed information about how movies are categorized for debugging
         * purposes
         * This method provides insight into which movies are in which categories and
         * why
         *
         * @return Map containing categorization details
         */
        java.util.Map<String, Object> getCategorizationDebugInfo();

        /**
         * Update all schedules with null status to use "AUTO" status for dynamic
         * calculation
         * This is a utility method to migrate existing data to the new hybrid system
         *
         * @return Number of schedules updated
         */
        int updateNullStatusesToAuto();

        /**
         * Cập nhật trạng thái của lịch chiếu đã kết thúc từ ACTIVE thành INACTIVE
         *
         * @return Số lượng lịch chiếu đã được cập nhật
         */
        int updateExpiredScheduleStatuses();

        /**
         * Cập nhật trạng thái của lịch chiếu từ UPCOMING thành ACTIVE
         * khi thời gian hiện tại đã đến hoặc vượt qua thời gian bắt đầu
         *
         * @return Số lượng lịch chiếu đã được cập nhật
         */
        int updateUpcomingToActiveSchedules();

        /**
         * Lưu nhiều lịch chiếu cho cùng một bộ phim trong một lần
         *
         * @param baseSchedule Thông tin cơ bản chung (phim, ngày, giá vé)
         * @param timeSlots    Danh sách thông tin suất chiếu (giờ bắt đầu, giờ kết
         *                     thúc, phòng chiếu)
         * @return Danh sách lịch chiếu đã lưu
         */
        List<ScreeningScheduleDto> saveBatchSchedules(ScreeningScheduleDto baseSchedule,
                        List<Map<String, Object>> timeSlots)
                        throws ScheduleConflictException;

        /**
         * Lấy lịch chiếu của phim theo ID và trạng thái
         *
         * @param movieId ID của phim
         * @param status  Trạng thái lịch chiếu cần lọc
         * @return Danh sách lịch chiếu đã lọc
         */
        List<ScreeningScheduleDto> getSchedulesByMovieIdAndStatus(Integer movieId, String status);
}
