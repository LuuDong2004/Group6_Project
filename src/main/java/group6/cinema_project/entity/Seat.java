package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String row;
    @Column(name = "seat_type")
    private String type;
    // private double price;
    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    private ScreeningRoom room;
    private String status;

}
