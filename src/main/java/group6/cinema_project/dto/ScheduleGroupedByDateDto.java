
package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGroupedByDateDto {

    private LocalDate date;
    private List<ScheduleGroupedByRoomDto> rooms;

    // Helper methods for display
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        return date.format(formatter);
    }

    public int getRoomCount() {
        return rooms != null ? rooms.size() : 0;
    }

    public int getTotalTimeSlots() {
        if (rooms == null) return 0;
        return rooms.stream()
                .mapToInt(ScheduleGroupedByRoomDto::getTimeSlotCount)
                .sum();
    }

    public boolean hasRooms() {
        return rooms != null && !rooms.isEmpty();
    }
}
