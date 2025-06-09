package group6.cinema_project.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InvoiceDto {
    private int id;
    private LocalDateTime createdTime;
    private UserDto user;
    private EmployeeDto employee;


}