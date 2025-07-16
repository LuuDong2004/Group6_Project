package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Branch")
@NoArgsConstructor
@Data
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String address;

}
