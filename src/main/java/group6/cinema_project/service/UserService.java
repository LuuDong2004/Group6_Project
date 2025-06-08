package group6.cinema_project.service;

// UserService.java

import group6.cinema_project.dto.ChangePasswordDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.dto.UserLoginDto;
import group6.cinema_project.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {

    // Registration
    UserDto registerUser(UserRegistrationDto registrationDto);

    // Login
    UserDto loginUser(UserLoginDto loginDto);

    // User management
    UserDto getUserById(int id);
    UserDto getUserByUsername(String userName);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();

    // Update user
    UserDto updateUser(int id, UserDto userDto);
    UserDto updateUserProfile(String email, UserDto userDto);
    boolean changePassword(String email, ChangePasswordDto changePasswordDto);


    // Delete user
    boolean deleteUser(int id);


    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    // Validation methods
    boolean isUsernameExists(String userName);
    boolean isEmailExists(String email);
    boolean isPhoneExists(String phone);
}




