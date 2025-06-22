package group6.cinema_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for Seat.
 * Used for managing seats in the cinema booking system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {

    private Integer id;

    @Size(max = 255, message = "Seat name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Row is required")
    @Size(max = 1, message = "Row must be a single character")
    private String row;

    @NotNull(message = "Screening room is required")
    private Integer screeningRoomId;

    @Size(max = 20, message = "Seat type cannot exceed 20 characters")
    private String seatType; // REGULAR, VIP, PREMIUM, DISABLED_ACCESS

    @Size(max = 20, message = "Status cannot exceed 20 characters")
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE, OUT_OF_ORDER

    // Related entity information for display purposes
    private String screeningRoomName;
    private String branchName;

    // Seat availability information for specific screening
    private boolean isAvailable;
    private boolean isReserved;
    private boolean isBooked;
    private Integer reservationId;
    private Integer ticketId;

    // Lists for related entities
    private List<SeatReservationDto> seatReservations;
    private List<TicketDto> tickets;

    // Helper methods
    public String getFormattedSeatPosition() {
        return row + name;
    }

    public boolean isVip() {
        return "VIP".equals(seatType);
    }

    public boolean isPremium() {
        return "PREMIUM".equals(seatType);
    }

    public boolean isRegular() {
        return "REGULAR".equals(seatType);
    }

    public boolean isDisabledAccess() {
        return "DISABLED_ACCESS".equals(seatType);
    }

    public boolean isAvailableForBooking() {
        return "AVAILABLE".equals(status) && !isReserved && !isBooked;
    }

    public boolean isOutOfOrder() {
        return "OUT_OF_ORDER".equals(status) || "MAINTENANCE".equals(status);
    }
}