package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "showtimes", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_showtime_hall_date_slot", 
                           columnNames = {"hall_id", "show_date", "time_slot_id"})
       },
       indexes = {
           @Index(name = "ix_showtimes_movie_date", columnList = "movie_id, show_date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"showtimeSeatStatuses", "bookings"})
public class Showtime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall hall;
    
    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;
    
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShowtimeSeatStatus> showtimeSeatStatuses;
    
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
} 