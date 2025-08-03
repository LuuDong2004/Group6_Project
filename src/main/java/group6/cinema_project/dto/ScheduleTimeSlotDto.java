
package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import group6.cinema_project.entity.Enum.ScheduleStatus;

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
    private ScheduleStatus status;

    // Helper methods for display
    public String getFormattedTimeRange() {
        return startTime + " - " + endTime;
    }

    public String getStatusClass() {
        if (status == ScheduleStatus.ACTIVE) {
            return "active";
        } else if (status == ScheduleStatus.UPCOMING) {
            return "upcoming";
        } else {
            return "ended";
        }
    }

    public String getStatusDisplayText() {
        if (status == null) {
            return "Không xác định";
        }
        return status.getDisplayName();
    }

    public boolean isEditable() {
        return status != null && status.isEditable();
    }
}
