package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "showtime_seat_status",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_showtime_seat", columnNames = {"showtime_id", "seat_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"bookingTickets"})
public class ShowtimeSeatStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "Available";
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @OneToMany(mappedBy = "showtimeSeat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingTicket> bookingTickets;
} 