package group6.cinema_project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
public class ScreeningScheduleDto {
    private Integer id;
    // IDs for input/output
    @NotNull(message = "Phim không được để trống")
    private Integer movieId;
    @NotNull(message = "Phòng chiếu không được để trống")
    private Integer screeningRoomId;
    @NotNull(message = "Chi nhánh không được để trống")
    private Integer branchId;
    @NotNull(message = "Ngày chiếu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate screeningDate;
    @NotNull(message = "Giờ bắt đầu không được để trống")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @NotNull(message = "Giờ kết thúc không được để trống")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @NotBlank(message = "Trạng thái không được để trống")
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
