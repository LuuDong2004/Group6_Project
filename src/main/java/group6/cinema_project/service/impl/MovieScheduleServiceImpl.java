package group6.cinema_project.service.impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.exception.ScheduleConflictException;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.repository.MovieScheduleRepository;
import group6.cinema_project.repository.ScreeningRoomRepository;
import group6.cinema_project.service.MovieScheduleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieScheduleServiceImpl implements MovieScheduleService {

    private final MovieScheduleRepository movieScheduleRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRoomRepository screeningRoomRepository;
    private final ModelMapper modelMapper;

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
        
        // Calculate and set the correct end time based on movie duration
        calculateAndSetEndTime(screeningScheduleDto);

        ScreeningSchedule screeningSchedule = modelMapper.map(screeningScheduleDto, ScreeningSchedule.class);
        ScreeningSchedule savedSchedule = movieScheduleRepository.save(screeningSchedule);
        return convertToDto(savedSchedule);
    }

    @Override
    @Transactional
    public void deleteScreeningSchedule(Integer id) {
        if (!movieScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Screening schedule not found with ID: " + id);
        }
        // kiểm tra xem lịch chiếu có đang chiếu hay không nếu đang chiếu sẽ không cho xóa
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
    public List<ScreeningScheduleDto> getFilteredScreeningSchedules(String searchTerm, String filterBy) {
        // Basic implementation - can be enhanced based on requirements
        return getAllScreeningSchedules();
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
     * Convert ScreeningSchedule entity to DTO without related entity data
     */

    private ScreeningScheduleDto convertToDto(ScreeningSchedule screeningSchedule) {
        return modelMapper.map(screeningSchedule, ScreeningScheduleDto.class);
    }

    /**
     * Convert ScreeningSchedule entity to DTO with related entity data for display
     * This method manually maps only the required fields to avoid unwanted
     * relationship loading
     */
    private ScreeningScheduleDto convertToDtoWithRelatedData(ScreeningSchedule screeningSchedule) {
        ScreeningScheduleDto dto = new ScreeningScheduleDto();

        // Map basic screening schedule fields
        dto.setId(screeningSchedule.getId());
        dto.setMovieId(screeningSchedule.getMovieId());
        dto.setScreeningRoomId(screeningSchedule.getScreeningRoomId());
        dto.setBranchId(screeningSchedule.getBranchId());
        dto.setScreeningDate(screeningSchedule.getScreeningDate());
        dto.setStartTime(screeningSchedule.getStartTime());
        dto.setEndTime(screeningSchedule.getEndTime());
        dto.setStatus(screeningSchedule.getStatus());
        dto.setPrice(screeningSchedule.getPrice());

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
                    Optional<Movie> conflictMovieOpt = movieRepository.findById(conflictingSchedule.getMovieId());
                    if (conflictMovieOpt.isPresent()) {
                        conflictMovieName = conflictMovieOpt.get().getName();
                    }
                }

                conflicts.add(new ScheduleConflictException.ConflictingSchedule(
                        conflictingSchedule.getId(),
                        conflictMovieName,
                        conflictingSchedule.getStartTime(),
                        conflictingSchedule.getEndTime()));
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
            case "CURRENTLY_PLAYING":
            case "PLAYING":
                return getCurrentlyPlayingMovies();

            case "UPCOMING":
            case "COMING_SOON":
            case "COMINGSOON":
                return getComingSoonMovies();

            case "ENDED":
            case "STOPPED":
            case "STOPPED_SHOWING":
                return getStoppedShowingMovies();

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

   
}
