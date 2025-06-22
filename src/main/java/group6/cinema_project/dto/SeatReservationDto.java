package group6.cinema_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SeatReservation.
 * Used for managing seat reservations in the booking workflow.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationDto {
    
    private Integer id;
    
    @NotNull(message = "Screening schedule is required")
    private Integer screeningScheduleId;
    
    @NotNull(message = "Seat is required")
    private Integer seatId;
    
    private Integer ticketId;
    
    private String reservationStatus; // RESERVED, CONFIRMED, CANCELLED, EXPIRED
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservedUntil;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    // Related entity information for display purposes
    private String seatName;
    private String seatRow;
    private String seatType;
    
    private String movieName;
    private String movieImage;
    private LocalDateTime screeningDateTime;
    private String screeningRoomName;
    private String branchName;
    
    // Ticket information if available
    private String ticketQrCode;
    private Double ticketPrice;
    
    // Helper methods for reservation management
    public boolean isExpired() {
        return reservedUntil != null && LocalDateTime.now().isAfter(reservedUntil);
    }
    
    public boolean isConfirmed() {
        return "CONFIRMED".equals(reservationStatus);
    }
    
    public boolean isReserved() {
        return "RESERVED".equals(reservationStatus);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(reservationStatus);
    }
    
    // Get formatted seat position
    public String getFormattedSeatPosition() {
        return seatRow + seatName;
    }
    
    // Get remaining reservation time in minutes
    public long getRemainingReservationMinutes() {
        if (reservedUntil == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(reservedUntil)) return 0;
        return java.time.Duration.between(now, reservedUntil).toMinutes();
    }
}
