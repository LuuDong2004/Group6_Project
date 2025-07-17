package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.repository.Admin.AdminMovieRepository;
import group6.cinema_project.repository.Admin.AdminRoomRepository;
import group6.cinema_project.repository.Admin.AdminScheduleRepository;
import group6.cinema_project.repository.Admin.AdminBranchRepository;
import group6.cinema_project.service.Admin.IAdminScheduleService;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleGroupedByDateDto;
import group6.cinema_project.dto.ScheduleGroupedByRoomDto;
import group6.cinema_project.dto.ScheduleTimeSlotDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.Branch;
import group6.cinema_project.exception.ScheduleConflictException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminScheduleServiceImpl implements IAdminScheduleService {

    private final AdminScheduleRepository movieScheduleRepository;
    private final AdminMovieRepository movieRepository;
    private final AdminRoomRepository screeningRoomRepository;
    private final AdminBranchRepository branchRepository;
    List<ScheduleGroupedByDateDto> groupedSchedules;

    @Override
    @Transactional(readOnly = true)
    public Optional<ScreeningScheduleDto> getScreeningScheduleById(Integer id) {
        return movieScheduleRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public ScreeningScheduleDto saveOrUpdateScreeningSchedule(ScreeningScheduleDto screeningScheduleDto) {
        validateScheduleIsNotCurrentlyShowing(screeningScheduleDto.getId());

        calculateAndSetEndTime(screeningScheduleDto);

        ScreeningSchedule screeningSchedule = convertToEntity(screeningScheduleDto);
        ScreeningSchedule savedSchedule = movieScheduleRepository.save(screeningSchedule);
        return convertToDto(savedSchedule);
    }

    @Override
    @Transactional
    public void deleteScreeningSchedule(Integer id) {
        if (!movieScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Screening schedule not found with ID: " + id);
        }
        // kiểm tra xem lịch chiếu có đang chiếu hay không nếu đang chiếu sẽ không cho
        // xóa
        validateScheduleIsNotCurrentlyShowing(id);

        movieScheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningScheduleDto> getAllScreeningSchedules() {
        return movieScheduleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningScheduleDto> getAllScreeningSchedulesForDisplay() {
        List<ScreeningSchedule> schedules = movieScheduleRepository.findAllWithRelatedEntities();
        return schedules.stream()
                .map(this::convertToDtoWithRelatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningScheduleDto> getFilteredScreeningSchedulesForDisplay(
            Integer movieId, LocalDate screeningDate, Integer screeningRoomId) {
        List<ScreeningSchedule> schedules = movieScheduleRepository.findFilteredWithRelatedEntities(
                movieId, screeningDate, screeningRoomId);
        return schedules.stream()
                .map(this::convertToDtoWithRelatedData)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to safely convert java.util.Date to LocalDate
     */
    private LocalDate convertDateToLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }

        // Handle both java.sql.Date and java.util.Date
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            // For java.util.Date, convert via Instant
            return date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
    }

    /**
     * Convert ScreeningSchedule entity to DTO without related entity data
     */

    private ScreeningScheduleDto convertToDto(ScreeningSchedule screeningSchedule) {
        ScreeningScheduleDto dto = new ScreeningScheduleDto();
        dto.setId(screeningSchedule.getId());
        if (screeningSchedule.getMovie() != null) {
            dto.setMovieId(screeningSchedule.getMovie().getId());
        }
        if (screeningSchedule.getScreeningRoom() != null) {
            dto.setScreeningRoomId(screeningSchedule.getScreeningRoom().getId());
        }
        if (screeningSchedule.getBranch() != null) {
            dto.setBranchId(screeningSchedule.getBranch().getId());
        }
        if (screeningSchedule.getScreeningDate() != null) {
            // Convert java.util.Date to LocalDate safely
            dto.setScreeningDate(convertDateToLocalDate(screeningSchedule.getScreeningDate()));
        }
        if (screeningSchedule.getStartTime() != null) {
            dto.setStartTime(screeningSchedule.getStartTime().toLocalTime());
        }
        if (screeningSchedule.getEndTime() != null) {
            dto.setEndTime(screeningSchedule.getEndTime().toLocalTime());
        }
        dto.setStatus(screeningSchedule.getStatus());
        // availableSeats and display fields are set elsewhere if needed
        return dto;
    }

    private ScreeningSchedule convertToEntity(ScreeningScheduleDto dto) {
        ScreeningSchedule entity = new ScreeningSchedule();
        entity.setId(dto.getId());
        if (dto.getMovieId() != null) {
            Movie movie = movieRepository.findById(dto.getMovieId()).orElse(null);
            entity.setMovie(movie);
        }
        if (dto.getScreeningRoomId() != null) {
            ScreeningRoom room = screeningRoomRepository.findById(dto.getScreeningRoomId()).orElse(null);
            entity.setScreeningRoom(room);
        }
        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId()).orElse(null);
            entity.setBranch(branch);
        }
        if (dto.getScreeningDate() != null) {
            entity.setScreeningDate(java.sql.Date.valueOf(dto.getScreeningDate()));
        }
        if (dto.getStartTime() != null) {
            entity.setStartTime(java.sql.Time.valueOf(dto.getStartTime()));
        }
        if (dto.getEndTime() != null) {
            entity.setEndTime(java.sql.Time.valueOf(dto.getEndTime()));
        }
        entity.setStatus(dto.getStatus());
        return entity;
    }

    /**
     * Convert ScreeningSchedule entity to DTO with related entity data for display
     * This method manually maps only the required fields to avoid unwanted
     * relationship loading
     */
    private ScreeningScheduleDto convertToDtoWithRelatedData(ScreeningSchedule screeningSchedule) {
        ScreeningScheduleDto dto = convertToDto(screeningSchedule);
        // Set movie information
        if (screeningSchedule.getMovie() != null) {
            dto.setMovieName(screeningSchedule.getMovie().getName());
            dto.setMovieImage(screeningSchedule.getMovie().getImage());
            dto.setMovieDuration(screeningSchedule.getMovie().getDuration());
            dto.setMovieRating(screeningSchedule.getMovie().getRating());
            dto.setMovieGenre(screeningSchedule.getMovie().getGenre());
        }
        // Set screening room information
        if (screeningSchedule.getScreeningRoom() != null) {
            dto.setScreeningRoomName(screeningSchedule.getScreeningRoom().getName());
            dto.setScreeningRoomCapacity(screeningSchedule.getScreeningRoom().getCapacity());
        }
        // Set branch information
        if (screeningSchedule.getBranch() != null) {
            dto.setBranchName(screeningSchedule.getBranch().getName());
            dto.setBranchAddress(screeningSchedule.getBranch().getAddress());
        }
        return dto;
    }

    /**
     * Calculate and set the correct end time based on movie duration
     */
    private void calculateAndSetEndTime(ScreeningScheduleDto screeningScheduleDto) {
        if (screeningScheduleDto.getMovieId() != null && screeningScheduleDto.getStartTime() != null) {
            Optional<Movie> movieOpt = movieRepository.findById(screeningScheduleDto.getMovieId());
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                Integer duration = movie.getDuration();
                if (duration != null && duration > 0) {
                    LocalTime calculatedEndTime = screeningScheduleDto.getStartTime().plusMinutes(duration);
                    screeningScheduleDto.setEndTime(calculatedEndTime);
                    // Also set movie duration in DTO for display purposes
                    screeningScheduleDto.setMovieDuration(duration);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateScheduleConflicts(ScreeningScheduleDto screeningScheduleDto) throws ScheduleConflictException {
        // Get movie duration to calculate actual end time
        Optional<Movie> movieOpt = movieRepository.findById(screeningScheduleDto.getMovieId());
        if (movieOpt.isEmpty()) {
            throw new IllegalArgumentException("Movie not found with ID: " + screeningScheduleDto.getMovieId());
        }

        Movie movie = movieOpt.get();
        Integer movieDuration = movie.getDuration(); // Duration in minutes

        if (movieDuration == null || movieDuration <= 0) {
            throw new IllegalArgumentException("Movie duration is not valid for movie: " + movie.getName());
        }

        // Calculate actual end time based on start time + movie duration
        LocalTime startTime = screeningScheduleDto.getStartTime();
        LocalTime calculatedEndTime = startTime.plusMinutes(movieDuration);

        // Get screening room information for error messages
        Optional<ScreeningRoom> roomOpt = screeningRoomRepository.findById(screeningScheduleDto.getScreeningRoomId());
        String roomName = roomOpt.map(ScreeningRoom::getName).orElse("Unknown Room");

        // Find overlapping schedules
        // Convert LocalTime to String format for SQL Server compatibility
        String startTimeStr = startTime.toString();
        String endTimeStr = calculatedEndTime.toString();

        List<ScreeningSchedule> overlappingSchedules;
        try {
            overlappingSchedules = movieScheduleRepository.findOverlappingSchedules(
                    screeningScheduleDto.getScreeningRoomId(),
                    screeningScheduleDto.getScreeningDate(),
                    startTimeStr,
                    endTimeStr,
                    screeningScheduleDto.getId() // Exclude current schedule for updates
            );
        } catch (Exception e) {
            throw new RuntimeException("Error checking for schedule conflicts: " + e.getMessage(), e);
        }

        if (!overlappingSchedules.isEmpty()) {
            // Create conflict details
            List<ScheduleConflictException.ConflictingSchedule> conflicts = new ArrayList<>();
            for (ScreeningSchedule conflictingSchedule : overlappingSchedules) {
                String conflictMovieName = "Unknown Movie";
                if (conflictingSchedule.getMovie() != null) {
                    conflictMovieName = conflictingSchedule.getMovie().getName();
                } else {
                    // Fallback: load movie separately if not loaded
                    Optional<Movie> conflictMovieOpt = movieRepository.findById(conflictingSchedule.getMovie().getId());
                    if (conflictMovieOpt.isPresent()) {
                        conflictMovieName = conflictMovieOpt.get().getName();
                    }
                }

                conflicts.add(new ScheduleConflictException.ConflictingSchedule(
                        conflictingSchedule.getId(),
                        conflictMovieName,
                        conflictingSchedule.getStartTime() != null ? conflictingSchedule.getStartTime().toLocalTime()
                                : null,
                        conflictingSchedule.getEndTime() != null ? conflictingSchedule.getEndTime().toLocalTime()
                                : null));
            }

            throw new ScheduleConflictException(
                    "Schedule conflict detected",
                    screeningScheduleDto.getScreeningRoomId(),
                    roomName,
                    screeningScheduleDto.getScreeningDate(),
                    startTime,
                    calculatedEndTime,
                    conflicts);
        }
    }

    /**
     * Validate that the screening date and time are not in the past
     *
     * @param screeningScheduleDto The schedule to validate
     * @throws IllegalArgumentException if the date or time is in the past
     */
    private void validateDateTimeNotInPast(ScreeningScheduleDto screeningScheduleDto) {
        LocalDate screeningDate = screeningScheduleDto.getScreeningDate();
        LocalTime startTime = screeningScheduleDto.getStartTime();

        if (screeningDate == null || startTime == null) {
            return; // Let other validators handle null values
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Check if the date is in the past
        if (screeningDate.isBefore(today)) {
            throw new IllegalArgumentException("Không thể tạo lịch chiếu cho ngày trong quá khứ: " + screeningDate);
        }

        // If the date is today, check if the time is in the past
        if (screeningDate.equals(today) && startTime.isBefore(currentTime)) {
            throw new IllegalArgumentException(
                    "Không thể tạo lịch chiếu cho thời gian trong quá khứ: " + startTime + " hôm nay");
        }
    }

    @Override
    @Transactional
    public ScreeningScheduleDto saveOrUpdateScreeningScheduleWithValidation(ScreeningScheduleDto screeningScheduleDto)
            throws ScheduleConflictException {
        // Validate date and time are not in the past
        validateDateTimeNotInPast(screeningScheduleDto);

        // Validate for conflicts
        validateScheduleConflicts(screeningScheduleDto);

        // If no conflicts, proceed with save
        return saveOrUpdateScreeningSchedule(screeningScheduleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesByScheduleStatus(String status) {
        // Use dynamic date-based categorization instead of static status field
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return getMoviesWithActiveSchedules(); // Changed to use the new method
            case "UPCOMING":
                return getComingSoonMovies();
            case "ENDED":
                return getMoviesWithEndedSchedules(); // Changed to use the new method

            default:
                // For backward compatibility, try the old static status approach as fallback
                System.err.println("Unknown status: " + status + ". Using fallback to static status query.");
                try {
                    List<Movie> movies = movieScheduleRepository.findMoviesByScheduleStatus(status);
                    return movies.stream()
                            .map(this::convertMovieToBasicDto)
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    System.err.println(
                            "Fallback static status query failed for status " + status + ": " + e.getMessage());
                    e.printStackTrace();
                    return new ArrayList<>();
                }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningScheduleDto> getSchedulesByMovieId(Integer movieId) {
        try {
            List<ScreeningSchedule> schedules = movieScheduleRepository.findByMovieIdWithRelatedEntities(movieId);
            return schedules.stream()
                    .map(this::convertToDtoWithRelatedData)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching schedules for movie " + movieId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleGroupedByDateDto> getSchedulesByMovieIdGrouped(Integer movieId) {
        try {
            List<ScreeningScheduleDto> schedules = getSchedulesByMovieId(movieId);

            // Group schedules by date and then by room name in a single, more efficient
            // operation.
            Map<LocalDate, Map<String, List<ScreeningScheduleDto>>> groupedByDateAndRoom = schedules.stream()
                    .collect(Collectors.groupingBy(
                            ScreeningScheduleDto::getScreeningDate,
                            Collectors.groupingBy(ScreeningScheduleDto::getScreeningRoomName)));

            // Transform the nested map into the desired DTO structure.
            return groupedByDateAndRoom.entrySet().stream()
                    .map(dateEntry -> {
                        List<ScheduleGroupedByRoomDto> rooms = dateEntry.getValue().entrySet().stream()
                                .map(roomEntry -> {
                                    List<ScreeningScheduleDto> roomSchedules = roomEntry.getValue();
                                    // All schedules in this group share the same branch, so we can take it from the
                                    // first one.
                                    String branchName = roomSchedules.get(0).getBranchName();

                                    List<ScheduleTimeSlotDto> timeSlots = roomSchedules.stream()
                                            .map(schedule -> new ScheduleTimeSlotDto(
                                                    schedule.getId(),
                                                    schedule.getStartTime(),
                                                    schedule.getEndTime(),
                                                    schedule.getStatus()))
                                            .sorted(Comparator.comparing(ScheduleTimeSlotDto::getStartTime))
                                            .collect(Collectors.toList());

                                    return new ScheduleGroupedByRoomDto(roomEntry.getKey(), branchName, timeSlots);
                                })
                                .sorted(Comparator.comparing(ScheduleGroupedByRoomDto::getRoomName))
                                .collect(Collectors.toList());

                        return new ScheduleGroupedByDateDto(dateEntry.getKey(), rooms);
                    })
                    .sorted(Comparator.comparing(ScheduleGroupedByDateDto::getDate))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error fetching grouped schedules for movie " + movieId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Convert Movie entity to basic MovieDto without triggering lazy loading
     * This method manually maps only the basic fields to avoid lazy loading issues
     */
    private MovieDto convertMovieToBasicDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map only basic fields that exist in Movie entity
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setRating(movie.getRating());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getCurrentlyPlayingMovies() {
        try {
            List<Movie> movies = movieScheduleRepository.findCurrentlyPlayingMovies();
            return movies.stream()
                    .map(this::convertMovieToBasicDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching currently playing movies: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getComingSoonMovies() {
        try {
            List<Movie> movies = movieScheduleRepository.findComingSoonMovies();
            return movies.stream()
                    .map(this::convertMovieToBasicDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching coming soon movies: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getStoppedShowingMovies() {
        try {
            List<Movie> movies = movieScheduleRepository.findStoppedShowingMovies();
            return movies.stream()
                    .map(this::convertMovieToBasicDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching stopped showing movies: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesWithEndedSchedules() {
        try {
            List<Movie> movies = movieScheduleRepository.findMoviesWithEndedSchedules();
            return movies.stream()
                    .map(this::convertMovieToBasicDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching movies with ended schedules: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesWithActiveSchedules() {
        try {
            List<Movie> movies = movieScheduleRepository.findMoviesWithActiveSchedules();
            return movies.stream()
                    .map(this::convertMovieToBasicDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error and return empty list to prevent application crash
            System.err.println("Error fetching movies with active schedules: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getCategorizationDebugInfo() {
        java.util.Map<String, Object> debugInfo = new java.util.HashMap<>();

        try {
            // Get counts for each category
            List<MovieDto> currentlyPlaying = getCurrentlyPlayingMovies();
            List<MovieDto> comingSoon = getComingSoonMovies();
            List<MovieDto> stoppedShowing = getStoppedShowingMovies();

            debugInfo.put("currentlyPlayingCount", currentlyPlaying.size());
            debugInfo.put("comingSoonCount", comingSoon.size());
            debugInfo.put("stoppedShowingCount", stoppedShowing.size());
            debugInfo.put("totalMoviesWithSchedules",
                    currentlyPlaying.size() + comingSoon.size() + stoppedShowing.size());

            // Get detailed movie lists
            debugInfo.put("currentlyPlayingMovies", currentlyPlaying.stream()
                    .map(m -> m.getName() + " (ID: " + m.getId() + ")")
                    .collect(Collectors.toList()));
            debugInfo.put("comingSoonMovies", comingSoon.stream()
                    .map(m -> m.getName() + " (ID: " + m.getId() + ")")
                    .collect(Collectors.toList()));
            debugInfo.put("stoppedShowingMovies", stoppedShowing.stream()
                    .map(m -> m.getName() + " (ID: " + m.getId() + ")")
                    .collect(Collectors.toList()));

            // Check for overlaps (should be none)
            java.util.Set<Integer> currentlyPlayingIds = currentlyPlaying.stream()
                    .map(MovieDto::getId).collect(Collectors.toSet());
            java.util.Set<Integer> comingSoonIds = comingSoon.stream()
                    .map(MovieDto::getId).collect(Collectors.toSet());
            java.util.Set<Integer> stoppedShowingIds = stoppedShowing.stream()
                    .map(MovieDto::getId).collect(Collectors.toSet());

            java.util.List<String> overlaps = new ArrayList<>();
            currentlyPlayingIds.retainAll(comingSoonIds);
            if (!currentlyPlayingIds.isEmpty()) {
                overlaps.add("Currently Playing & Coming Soon: " + currentlyPlayingIds);
            }

            currentlyPlayingIds = currentlyPlaying.stream().map(MovieDto::getId).collect(Collectors.toSet());
            currentlyPlayingIds.retainAll(stoppedShowingIds);
            if (!currentlyPlayingIds.isEmpty()) {
                overlaps.add("Currently Playing & Stopped Showing: " + currentlyPlayingIds);
            }

            comingSoonIds.retainAll(stoppedShowingIds);
            if (!comingSoonIds.isEmpty()) {
                overlaps.add("Coming Soon & Stopped Showing: " + comingSoonIds);
            }

            debugInfo.put("overlaps", overlaps);
            debugInfo.put("hasOverlaps", !overlaps.isEmpty());

            // Get schedule status distribution
            List<ScreeningSchedule> allSchedules = movieScheduleRepository.findAllWithRelatedEntities();
            java.util.Map<String, Long> statusDistribution = allSchedules.stream()
                    .collect(Collectors.groupingBy(
                            schedule -> schedule.getStatus() != null ? schedule.getStatus() : "NULL",
                            Collectors.counting()));
            debugInfo.put("scheduleStatusDistribution", statusDistribution);

            debugInfo.put("timestamp", java.time.LocalDateTime.now().toString());

        } catch (Exception e) {
            debugInfo.put("error", "Error generating debug info: " + e.getMessage());
            System.err.println("Error generating categorization debug info: " + e.getMessage());
            e.printStackTrace();
        }

        return debugInfo;
    }

    @Override
    @Transactional
    public int updateNullStatusesToAuto() {
        try {
            // Find all schedules with null status
            List<ScreeningSchedule> allSchedules = movieScheduleRepository.findAll();
            int updatedCount = 0;

            for (ScreeningSchedule schedule : allSchedules) {
                if (schedule.getStatus() == null || schedule.getStatus().trim().isEmpty()) {
                    schedule.setStatus("AUTO");
                    movieScheduleRepository.save(schedule);
                    updatedCount++;
                }
            }

            System.out.println("Updated " + updatedCount + " schedules from null status to AUTO");
            return updatedCount;

        } catch (Exception e) {
            System.err.println("Error updating null statuses to AUTO: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // @Override
    @Transactional
    private void validateScheduleIsNotCurrentlyShowing(Integer id) {

        if (id == null) {
            return; // New schedule, no need to validate
        }

        Optional<ScreeningSchedule> scheduleOpt = movieScheduleRepository.findById(id);
        if (scheduleOpt.isPresent()) {
            ScreeningSchedule schedule = scheduleOpt.get();

            // Also check English status variants
            if ("ACTIVE".equalsIgnoreCase(schedule.getStatus())) {
                throw new IllegalStateException("Cannot modify or delete a schedule that is currently showing");
            }
        }
    }

    /**
     * Cập nhật tự động trạng thái lịch chiếu
     * Phương thức này sẽ tìm tất cả các lịch chiếu đã kết thúc nhưng vẫn có trạng
     * thái ACTIVE
     * và cập nhật chúng thành INACTIVE
     */
    @Override
    @Transactional
    public int updateExpiredScheduleStatuses() {

        try {
            // Tìm tất cả lịch chiếu đã kết thúc nhưng vẫn có trạng thái ACTIVE
            List<ScreeningSchedule> expiredSchedules = movieScheduleRepository.findExpiredActiveSchedules();
            int updatedCount = 0;

            for (ScreeningSchedule schedule : expiredSchedules) {
                schedule.setStatus("ENDED");
                movieScheduleRepository.save(schedule);
                updatedCount++;
            }

            if (updatedCount > 0) {
                System.out.println("Đã cập nhật " + updatedCount + " lịch chiếu từ ACTIVE thành ENDED");
            }

            return updatedCount;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái lịch chiếu đã hết hạn: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Cập nhật tự động trạng thái lịch chiếu từ UPCOMING thành ACTIVE
     * Phương thức này sẽ tìm tất cả các lịch chiếu đã đến hoặc vượt qua thời gian
     * bắt đầu
     * nhưng vẫn có trạng thái UPCOMING và cập nhật chúng thành ACTIVE
     */
    @Override
    @Transactional
    public int updateUpcomingToActiveSchedules() {
        try {
            // Tìm tất cả lịch chiếu đã đến thời gian chiếu nhưng vẫn có trạng thái UPCOMING
            List<ScreeningSchedule> schedulesShouldBeActive = movieScheduleRepository
                    .findUpcomingSchedulesThatShouldBeActive();
            int updatedCount = 0;

            for (ScreeningSchedule schedule : schedulesShouldBeActive) {
                schedule.setStatus("ACTIVE");
                movieScheduleRepository.save(schedule);
                updatedCount++;
            }

            if (updatedCount > 0) {
                System.out.println("Đã cập nhật " + updatedCount + " lịch chiếu từ UPCOMING thành ACTIVE");
            }

            return updatedCount;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái lịch chiếu từ UPCOMING thành ACTIVE: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @Transactional
    public List<ScreeningScheduleDto> saveBatchSchedules(ScreeningScheduleDto baseSchedule,
            List<Map<String, Object>> timeSlots)
            throws ScheduleConflictException {

        List<ScreeningScheduleDto> savedSchedules = new ArrayList<>();

        if (timeSlots == null || timeSlots.isEmpty()) {
            throw new IllegalArgumentException("Danh sách suất chiếu không được trống");
        }

        // Kiểm tra xem các trường bắt buộc của baseSchedule có hợp lệ không
        if (baseSchedule.getMovieId() == null || baseSchedule.getScreeningDate() == null) {
            throw new IllegalArgumentException("Thông tin cơ bản của lịch chiếu không đầy đủ");
        }

        // Duyệt qua từng suất chiếu và lưu
        for (Map<String, Object> slot : timeSlots) {
            ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();

            // Sao chép thông tin cơ bản từ baseSchedule
            scheduleDto.setMovieId(baseSchedule.getMovieId());
            scheduleDto.setScreeningDate(baseSchedule.getScreeningDate());
            scheduleDto.setStatus(baseSchedule.getStatus());

            // Lấy thông tin từ slot
            scheduleDto.setStartTime(LocalTime.parse((String) slot.get("startTime")));
            scheduleDto.setEndTime(LocalTime.parse((String) slot.get("endTime")));
            scheduleDto.setScreeningRoomId(Integer.valueOf((String) slot.get("roomId")));
            scheduleDto.setBranchId(Integer.valueOf((String) slot.get("branchId")));

            // Kiểm tra xung đột lịch chiếu
            validateScheduleConflicts(scheduleDto);

            // Lưu lịch chiếu
            ScreeningScheduleDto saved = saveOrUpdateScreeningSchedule(scheduleDto);
            savedSchedules.add(saved);
        }

        return savedSchedules;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningScheduleDto> getSchedulesByMovieIdAndStatus(Integer movieId, String status) {
        try {
            List<ScreeningSchedule> schedules;

            // Sử dụng logic động dựa trên trạng thái yêu cầu
            switch (status.toUpperCase()) {
                case "ENDED":
                    schedules = movieScheduleRepository.findEndedSchedulesByMovieIdWithRelatedEntities(movieId);
                    break;
                case "ACTIVE":
                    schedules = movieScheduleRepository.findActiveSchedulesByMovieIdWithRelatedEntities(movieId);
                    break;
                case "UPCOMING":
                    schedules = movieScheduleRepository.findUpcomingSchedulesByMovieIdWithRelatedEntities(movieId);
                    break;
                default:
                    // Fallback to original method for other statuses
                    schedules = movieScheduleRepository.findByMovieIdAndStatusWithRelatedEntities(movieId, status);
                    break;
            }

            return schedules.stream()
                    .map(this::convertToDtoWithRelatedData)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println(
                    "Error fetching schedules for movie " + movieId + " with status " + status + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
