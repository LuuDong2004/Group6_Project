package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Data
@Table(name = "Seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String row;

    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    private ScreeningRoom room;
    private Integer status;

}
