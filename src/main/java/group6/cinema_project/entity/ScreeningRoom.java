package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ScreeningRoom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int capacity; // sức chứa
    private String description;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;
}
