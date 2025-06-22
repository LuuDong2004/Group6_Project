package group6.cinema_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for ScreeningRoom.
 * Used for managing screening rooms in the cinema system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRoomDto {
    
    private Integer id;
    
    @NotBlank(message = "Room name is required")
    @Size(max = 255, message = "Room name cannot exceed 255 characters")
    private String name;
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    @NotNull(message = "Branch is required")
    private Integer branchId;
    
    // Related entity information for display purposes
    private String branchName;
    private String branchAddress;
    
    // Lists for related entities
    private List<SeatDto> seats;
    private List<ScreeningScheduleDto> screeningSchedules;
    
    // Additional room information
    private String roomType; // STANDARD, IMAX, 3D, 4DX, VIP
    private boolean hasAirConditioning;
    private boolean hasWheelchairAccess;
    private String soundSystem; // DOLBY_ATMOS, DTS, STANDARD
    private String screenType; // STANDARD, IMAX, 3D, 4K
    
    // Seat layout information
    private Integer totalSeats;
    private Integer vipSeats;
    private Integer regularSeats;
    private Integer disabledAccessSeats;
    private Integer numberOfRows;
    private Integer seatsPerRow;
    
    // Availability information
    private Integer availableSeats;
    private Integer bookedSeats;
    private boolean isActive;
    
    // Helper methods
    public boolean isImax() {
        return "IMAX".equals(roomType);
    }
    
    public boolean is3D() {
        return "3D".equals(roomType);
    }
    
    public boolean is4DX() {
        return "4DX".equals(roomType);
    }
    
    public boolean isVipRoom() {
        return "VIP".equals(roomType);
    }
    
    public boolean isStandard() {
        return "STANDARD".equals(roomType);
    }
    
    public double getOccupancyRate() {
        if (totalSeats == null || totalSeats == 0) return 0.0;
        return (double) (bookedSeats != null ? bookedSeats : 0) / totalSeats * 100;
    }
    
    public boolean isFullyBooked() {
        return totalSeats != null && bookedSeats != null && bookedSeats >= totalSeats;
    }
    
    public String getFormattedCapacity() {
        return totalSeats + " seats (" + 
               (vipSeats != null ? vipSeats : 0) + " VIP, " + 
               (regularSeats != null ? regularSeats : 0) + " Regular)";
    }
}
