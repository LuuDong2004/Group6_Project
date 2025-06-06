package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Data
@Entity
@Table(name = "ScreeningRoom")
@NoArgsConstructor
public class ScreeningRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int capacity; // sức chứa
    private String description;
    @ManyToOne
    @JoinColumn(name = "BranchId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;
}
