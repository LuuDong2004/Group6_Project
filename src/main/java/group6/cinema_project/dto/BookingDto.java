package group6.cinema_project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Booking.
 * Used for managing the complete booking workflow.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    
    private Integer id;
    
    @NotNull(message = "User is required")
    private Integer userId;
    
    @NotNull(message = "Screening schedule is required")
    private Integer screeningScheduleId;
    
    @NotBlank(message = "Booking code is required")
    @Size(max = 20, message = "Booking code cannot exceed 20 characters")
    private String bookingCode;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    private String bookingStatus; // PENDING, CONFIRMED, CANCELLED, EXPIRED, COMPLETED
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookingDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    // Related entity information for display purposes
    private String userName;
    private String userEmail;
    private String userPhone;
    
    private String movieName;
    private String movieImage;
    private String movieRating;
    private String movieGenre;
    private Integer movieDuration;
    
    private String screeningRoomName;
    private String branchName;
    private String branchAddress;
    
    private LocalDateTime screeningDateTime;
    private BigDecimal screeningPrice;
    
    // Lists for related entities
    private List<TicketDto> tickets;
    private List<SeatReservationDto> seatReservations;
    private InvoiceDto invoice;
    
    // Additional fields for booking management
    private Integer numberOfTickets;
    private List<String> selectedSeatNames;
    private String paymentMethod;
    private String paymentStatus;
    
    // Helper methods for booking workflow
    public boolean isPending() {
        return "PENDING".equals(bookingStatus);
    }
    
    public boolean isConfirmed() {
        return "CONFIRMED".equals(bookingStatus);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(bookingStatus);
    }
    
    public boolean isExpired() {
        return "EXPIRED".equals(bookingStatus) || 
               (expiryDate != null && LocalDateTime.now().isAfter(expiryDate));
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(bookingStatus);
    }
    
    // Get remaining time until expiry in minutes
    public long getRemainingExpiryMinutes() {
        if (expiryDate == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiryDate)) return 0;
        return java.time.Duration.between(now, expiryDate).toMinutes();
    }
    
    // Get formatted booking reference
    public String getFormattedBookingReference() {
        return bookingCode + "-" + id;
    }
    
    // Get total number of seats booked
    public int getTotalSeatsBooked() {
        return tickets != null ? tickets.size() : 0;
    }
}
