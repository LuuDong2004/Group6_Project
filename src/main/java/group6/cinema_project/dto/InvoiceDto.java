package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Invoice.
 * Used for managing invoices in the cinema booking system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDateTime;

    private Integer customerId;
    private Integer employeeId;
    private Integer bookingId;

    // Related entity information for display purposes
    private UserDto customer;
    private EmployeeDto employee;
    private BookingDto booking;

    // List of tickets associated with this invoice
    private List<TicketDto> tickets;

    // Additional invoice information
    private String invoiceNumber;
    private String paymentMethod;
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED

    // Helper methods
    public boolean isPaid() {
        return "PAID".equals(paymentStatus);
    }

    public boolean isPending() {
        return "PENDING".equals(paymentStatus);
    }

    public boolean isFailed() {
        return "FAILED".equals(paymentStatus);
    }

    public boolean isRefunded() {
        return "REFUNDED".equals(paymentStatus);
    }

    public String getFormattedInvoiceNumber() {
        return "INV-" + id + "-" + (invoiceNumber != null ? invoiceNumber : "");
    }
}