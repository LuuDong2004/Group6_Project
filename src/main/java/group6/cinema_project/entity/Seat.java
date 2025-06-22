package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "Seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "row", length = 1)
    private String row;

    @Column(name = "screening_room_id")
    private Integer screeningRoomId;

    @Column(name = "seat_type", length = 20)
    private String seatType;

    @Column(name = "status", length = 20)
    private String status;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_room_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ScreeningRoom screeningRoom;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SeatReservation> seatReservations;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Ticket> tickets;
}