package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

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
    @Column(name = "seat_type")
    private String type;
    // private double price;
    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningRoom room;
    private String status;

}
