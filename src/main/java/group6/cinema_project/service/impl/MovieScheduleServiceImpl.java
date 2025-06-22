package group6.cinema_project.service.impl;

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
@Transactional
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
    public ScreeningScheduleDto saveOrUpdateScreeningSchedule(ScreeningScheduleDto screeningScheduleDto) {
        // Calculate and set the correct end time based on movie duration
        calculateAndSetEndTime(screeningScheduleDto);

        ScreeningSchedule screeningSchedule = modelMapper.map(screeningScheduleDto, ScreeningSchedule.class);
        ScreeningSchedule savedSchedule = movieScheduleRepository.save(screeningSchedule);
        return convertToDto(savedSchedule);
    }

    @Override
    public void deleteScreeningSchedule(Integer id) {
        if (!movieScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Screening schedule not found with ID: " + id);
        }
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
    public ScreeningScheduleDto saveOrUpdateScreeningScheduleWithValidation(ScreeningScheduleDto screeningScheduleDto)
            throws ScheduleConflictException {
        // Validate date and time are not in the past
        validateDateTimeNotInPast(screeningScheduleDto);

        // Validate for conflicts
        validateScheduleConflicts(screeningScheduleDto);

        // If no conflicts, proceed with save
        return saveOrUpdateScreeningSchedule(screeningScheduleDto);
    }
}
