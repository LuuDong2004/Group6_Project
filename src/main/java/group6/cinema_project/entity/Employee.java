package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "EmployeeId")
    private String employeeId;
    private String job;
    private String salary;
}