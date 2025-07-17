
package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO representing a single time slot for a movie screening
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTimeSlotDto {

    private Integer id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;

    // Helper methods for display
    public String getFormattedTimeRange() {
        return startTime + " - " + endTime;
    }

    public String getStatusClass() {
        if ("ACTIVE".equals(status)) {
            return "active";
        } else if ("UPCOMING".equals(status)) {
            return "upcoming";
        } else {
            return "ended";
        }
    }

    public String getStatusDisplayText() {
        switch (status) {
            case "ACTIVE":
                return "Đang chiếu";
            case "UPCOMING":
                return "Sắp chiếu";
            case "ENDED":
                return "Đã kết thúc";
            default:
                return "Không xác định";
        }
    }

    public boolean isEditable() {
        return !"ACTIVE".equals(status) && !"ENDED".equals(status);
    }
}
