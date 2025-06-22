package group6.cinema_project.dto;

import group6.cinema_project.validation.FutureDate;
import group6.cinema_project.validation.FutureDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object for ScreeningSchedule.
 * Used for displaying and managing movie screening schedules.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FutureDateTime(message = "Thời gian chiếu không được là thời gian trong quá khứ")
public class ScreeningScheduleDto {

    private Integer id;

    @NotNull(message = "Movie is required")
    private Integer movieId;

    @NotNull(message = "Screening room is required")
    private Integer screeningRoomId;

    @NotNull(message = "Branch is required")
    private Integer branchId;

    @NotNull(message = "Screening date is required")
    @FutureDate(message = "Ngày chiếu không được là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate screeningDate;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Status is required")
    private String status;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    // Related entity information for display purposes
    private String movieName;
    private String movieImage;
    private Integer movieDuration;
    private String movieRating;
    private String movieGenre;

    private String screeningRoomName;
    private Integer screeningRoomCapacity;

    private String branchName;
    private String branchAddress;

    // Removed booking-related fields to avoid unnecessary relationships

    // Helper method to check if screening is in the past
    public boolean isPastScreening() {
        LocalDate today = LocalDate.now();
        return screeningDate.isBefore(today) ||
                (screeningDate.equals(today) && startTime.isBefore(LocalTime.now()));
    }

    // Helper method to get formatted screening time
    public String getFormattedScreeningTime() {
        return screeningDate + " " + startTime;
    }

    // Helper method to calculate end time based on movie duration
    public LocalTime getCalculatedEndTime() {
        if (startTime != null && movieDuration != null && movieDuration > 0) {
            return startTime.plusMinutes(movieDuration);
        }
        return endTime; // Fallback to provided end time
    }

    // Helper method to check if the calculated end time differs from provided end
    // time
    public boolean isEndTimeCalculated() {
        LocalTime calculated = getCalculatedEndTime();
        return calculated != null && !calculated.equals(endTime);
    }

    // Helper method to get duration in hours and minutes format
    public String getFormattedDuration() {
        if (movieDuration == null || movieDuration <= 0) {
            return "N/A";
        }
        int hours = movieDuration / 60;
        int minutes = movieDuration % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}
