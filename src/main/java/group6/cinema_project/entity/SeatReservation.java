package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "SeatReservation")
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "screening_schedule_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    @Column(name = "reservation_status")
    private String status;

    @Column(name = "reserved_until")
    private String reseredUnit;

    @Column(name = "created_date")
    private Date createDate;
}
