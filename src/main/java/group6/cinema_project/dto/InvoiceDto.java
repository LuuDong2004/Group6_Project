package group6.cinema_project.dto;

import group6.cinema_project.entity.Employee;
import group6.cinema_project.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public class InvoiceDto {
    private int id;
    private LocalDateTime createdTime;
    private User user;
    private Employee employee;


}
