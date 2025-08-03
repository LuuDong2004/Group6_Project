package group6.cinema_project.dto.Admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import group6.cinema_project.entity.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Admin quản lý user
 * Chứa đầy đủ thông tin cần thiết cho việc quản lý user
 */
@Data
@NoArgsConstructor
public class AdminUserDto {
    private Integer id;
    
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
    private String userName;

    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;
    
    private String address;
    
    @NotNull(message = "Vai trò không được để trống")
    private Role role;
    
    private String provider = "LOCAL"; // Default provider
    
    // Thông tin quản lý cho admin
    private LocalDateTime createdDate; // Ngày tạo tài khoản
    private LocalDateTime lastLoginDate; // Lần đăng nhập cuối
    private boolean isActive; // Trạng thái hoạt động
    private boolean isLocked; // Trạng thái khóa tài khoản
    private Integer totalBookings; // Tổng số booking
    private Double totalSpent; // Tổng số tiền đã chi tiêu
    private String lastLoginIp; // IP đăng nhập cuối
    private Integer loginAttempts; // Số lần thử đăng nhập
    
    // Thông tin thống kê
    private String membershipLevel; // Cấp độ thành viên
    private Integer loyaltyPoints; // Điểm tích lũy
    
    // Thông tin liên hệ khẩn cấp
    private String emergencyContact;
    private String emergencyPhone;
    
    // Ghi chú của admin
    private String adminNotes;
}
