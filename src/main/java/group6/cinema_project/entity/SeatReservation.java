package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_reservation", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"screening_schedule_id", "seat_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "screening_schedule_id", nullable = false)
    private Integer screeningScheduleId;
    
    @Column(name = "seat_id", nullable = false)
    private Integer seatId;
    
    @Column(name = "ticket_id")
    private Integer ticketId;
    
    @Column(name = "reservation_status", length = 20)
    private String reservationStatus;
    
    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_schedule_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningSchedule screeningSchedule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Ticket ticket;
}
