package group6.cinema_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Ticket.
 * Used for managing tickets in the cinema booking system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    private Integer id;

    @NotBlank(message = "QR code is required")
    @Size(max = 255, message = "QR code cannot exceed 255 characters")
    private String qrCode;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private Integer seatId;
    private Integer invoiceId;

    @NotNull(message = "Booking is required")
    private Integer bookingId;

    // Related entity information for display purposes
    private String seatName;
    private String seatRow;
    private String seatType;

    private String movieName;
    private String movieImage;
    private String movieRating;
    private Integer movieDuration;

    private String screeningRoomName;
    private String branchName;

    private String bookingCode;
    private String bookingStatus;

    // Invoice information
    private InvoiceDto invoice;

    // Seat reservation information
    private SeatReservationDto seatReservation;

    // Helper methods
    public String getFormattedSeatPosition() {
        return seatRow + seatName;
    }

    public boolean isValid() {
        return qrCode != null && !qrCode.trim().isEmpty();
    }

    public String getTicketReference() {
        return "TKT-" + id + "-" + qrCode.substring(0, Math.min(8, qrCode.length()));
    }
}