package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Region")
@NoArgsConstructor
@Getter
@Setter

public class Region {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String type; // Miền Bắc, Miền Trung, Miền Nam
    private String name; // Hà Nội, Đà Nẵng, TP.HCM, ...
}
