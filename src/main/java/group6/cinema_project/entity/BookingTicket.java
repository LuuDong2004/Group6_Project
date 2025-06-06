package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_seat_id", nullable = false)
    private ShowtimeSeatStatus showtimeSeat;
    
    @Column(name = "ticket_code", nullable = false, unique = true, length = 100)
    private String ticketCode;
    
    @Column(name = "price_at_booking", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtBooking;
} 