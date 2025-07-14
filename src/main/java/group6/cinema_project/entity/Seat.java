package group6.cinema_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@Table(name = "Seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "name", nullable = false, length = 50, columnDefinition = "NVARCHAR(50)")
    private String name;
    @Column(name = "row", nullable = false)
    private String row;
    @Column(name = "seat_type", nullable = false, length = 50, columnDefinition = "NVARCHAR(50)")
    private String seatType;
    @Column(name = "status", nullable = false, length = 50, columnDefinition = "NVARCHAR(50)")
    private String status;
    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    private ScreeningRoom room;

    public Seat() {
    }

    public Seat(int id, String name, String row, String seatType, String status, ScreeningRoom room) {
        this.id = id;
        this.name = name;
        this.row = row;
        this.seatType = seatType;
        this.status = status;
        this.room = room;
    }
}
