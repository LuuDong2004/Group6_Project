package group6.cinema_project.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private int id;
    private String username;

    private String password;


    private String dateOfBrith;
    private String email;
    private String phone;
    private String address;
    private String role;
}
