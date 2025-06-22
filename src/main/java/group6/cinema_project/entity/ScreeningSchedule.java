package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "screening_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "screening_room_id")
    private Integer screeningRoomId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "screening_date", nullable = false)
    private LocalDate screeningDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "status", nullable = false, length = 255)
    private String status;

    @Column(name = "price", nullable = false, precision = 20)
    private BigDecimal price;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_room_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ScreeningRoom screeningRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Branch branch;

    @OneToMany(mappedBy = "screeningSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Booking> bookings;

    @OneToMany(mappedBy = "screeningSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SeatReservation> seatReservations;

    
}