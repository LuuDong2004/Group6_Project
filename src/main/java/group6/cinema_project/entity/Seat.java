package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter

@Table(name = "Seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String row;
    private int status;
    @ManyToOne
    @JoinColumn(name = "screeningRoomId")
    private ScreeningRoom room;


}
