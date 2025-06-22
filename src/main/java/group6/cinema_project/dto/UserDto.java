package group6.cinema_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for User.
 * Used for managing users in the cinema booking system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer id;

    @Size(max = 255, message = "Username cannot exceed 255 characters")
    private String username;

    // Password should not be included in response DTOs for security
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String fullName;

    private String dateOfBirth;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 255, message = "Phone cannot exceed 255 characters")
    private String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 255, message = "Role cannot exceed 255 characters")
    private String role; // CUSTOMER, ADMIN, EMPLOYEE

    // Helper methods
    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equals(role);
    }

    public String getDisplayName() {
        return fullName != null && !fullName.trim().isEmpty() ? fullName : username;
    }
}