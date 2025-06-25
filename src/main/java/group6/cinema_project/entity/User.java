package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;

    private String password;

    @Column(name = "date_of_birth")
    private String dateOfBrith;
    private String email;
    private String phone;
    private String address;
    private String role;
}
