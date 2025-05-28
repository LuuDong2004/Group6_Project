package group6.cinema_project.entity;

import jakarta.persistence.*;
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
    private int id;

    private String name;
    private int row;
    @ManyToOne
    @JoinColumn(name = "screeningRoomId")
    private ScreeningRoom Room;

    public Seat() {
    }

    public Seat(int id, String name, int row, ScreeningRoom room) {
        this.id = id;
        this.name = name;
        this.row = row;
        Room = room;
    }
}
