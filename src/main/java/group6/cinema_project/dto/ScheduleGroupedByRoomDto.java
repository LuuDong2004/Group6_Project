package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing schedules grouped by room for a specific date
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGroupedByRoomDto {
    
    private String roomName;
    private String branchName;
    private List<ScheduleTimeSlotDto> timeSlots;
    
    // Helper methods
    public int getTimeSlotCount() {
        return timeSlots != null ? timeSlots.size() : 0;
    }
    
    public boolean hasTimeSlots() {
        return timeSlots != null && !timeSlots.isEmpty();
    }

   
}
