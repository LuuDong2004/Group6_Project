package group6.cinema_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Employee.
 * Used for managing employees in the cinema system.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Integer id;

    @Size(max = 255, message = "Employee ID cannot exceed 255 characters")
    private String employeeId;

    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title cannot exceed 255 characters")
    private String job;

    @Size(max = 255, message = "Salary cannot exceed 255 characters")
    private String salary;

    // Additional employee information
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String status; // ACTIVE, INACTIVE, ON_LEAVE

    // Helper methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isInactive() {
        return "INACTIVE".equals(status);
    }

    public boolean isOnLeave() {
        return "ON_LEAVE".equals(status);
    }

    public String getDisplayName() {
        return fullName != null && !fullName.trim().isEmpty() ? fullName : employeeId;
    }

    public String getFormattedEmployeeInfo() {
        return employeeId + " - " + job + (department != null ? " (" + department + ")" : "");
    }
}