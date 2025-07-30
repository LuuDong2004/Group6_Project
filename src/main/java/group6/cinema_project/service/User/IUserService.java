package group6.cinema_project.service.User;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import group6.cinema_project.dto.Login.AdminPasswordResetDto;
import group6.cinema_project.dto.Login.ChangePasswordDto;
import group6.cinema_project.dto.Login.PasswordResetConfirmDto;
import group6.cinema_project.dto.Login.PasswordResetRequestDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.dto.Login.UserLoginDto;
import group6.cinema_project.dto.Login.UserRegistrationDto;
import group6.cinema_project.entity.User;

public interface IUserService {
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

    void updateUserRole(int userId, String newRole);

    // Password reset
    boolean requestPasswordReset(PasswordResetRequestDto requestDto);

    boolean confirmPasswordReset(PasswordResetConfirmDto confirmDto);

    boolean validateResetToken(String token);

    // Delete user
    boolean deleteUser(int id);

    void deleteUserById(int id);

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    // Validation methods
    boolean isUsernameExists(String userName);

    boolean isEmailExists(String email);

    boolean isPhoneExists(String phone);

    // Trả về entity User theo email
    Optional<User> findByEmail(String email);

    String resetPassword(int userId);

    // Admin methods
    boolean adminResetPassword(AdminPasswordResetDto adminPasswordResetDto);
}
