package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String dateOfBrith;
    private String email;
    private String phone;
    private String address;
    private String role;
}
