package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Date")
public class Date {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String date;
    private String desciption;
}
