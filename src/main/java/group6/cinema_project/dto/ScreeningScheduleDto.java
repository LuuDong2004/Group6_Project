package group6.cinema_project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Data
public class ScreeningScheduleDto {
    private Integer id;
    // IDs for input/output
    private Integer movieId;
    private Integer screeningRoomId;
    private Integer branchId;
    private LocalDate screeningDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private int availableSeats;

    // Nested DTOs for display
    private MovieDto movie;
    private ScreeningRoomDto screeningRoom;
    private BranchDto branch;

    // Display fields for admin/frontend
    private String movieName;
    private String movieImage;
    private Integer movieDuration;
    private String movieRating;
    private String movieGenre;
    private String screeningRoomName;
    private Integer screeningRoomCapacity;
    private String branchName;
    private String branchAddress;

    // Formatted string fields for view rendering
    private String startTimeStr;
    private String endTimeStr;
    private String screeningDateStr;
}

