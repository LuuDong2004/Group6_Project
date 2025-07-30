package group6.cinema_project.dto;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import group6.cinema_project.entity.Enum.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private int id;
    private String userName;

    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String email;
    private String phone;
    private String address;
    private Role role;
    private String provider = "LOCAL"; // Default provider
}
