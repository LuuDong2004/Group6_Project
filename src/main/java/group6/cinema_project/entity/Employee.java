package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Thay đổi từ int thành Integer để nhất quán với các entity khác

    @Column(name = "EmployeeId")
    private String employeeId;

    private String job;

    private String salary;
}
