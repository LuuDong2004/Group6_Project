package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Seat")
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String row;
    private Integer status;
    @ManyToOne
    @JoinColumn(name = "screeningRoomId")
    private ScreeningRoom room;


}
