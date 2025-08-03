package group6.cinema_project.dto.Customer;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Customer profile
 * Chỉ chứa thông tin cần thiết cho customer quản lý profile của mình
 */
@Data
@NoArgsConstructor
public class CustomerUserDto {
    private Integer id;
    
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3-50 ký tự")
    private String userName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;
    
    private String address;
    
    // Thông tin hiển thị cho customer
    private String membershipLevel; // Cấp độ thành viên
    private Integer loyaltyPoints; // Điểm tích lũy
    private String memberSince; // Thành viên từ ngày (formatted)
    
    // Thống kê cá nhân
    private Integer totalBookings; // Tổng số vé đã đặt
    private Integer totalMoviesWatched; // Tổng số phim đã xem
    private String favoriteGenre; // Thể loại phim yêu thích
    
    // Thông tin bảo mật (chỉ hiển thị trạng thái)
    private boolean hasPassword; // Có mật khẩu hay không (cho OAuth users)
    private boolean twoFactorEnabled; // Có bật 2FA hay không
    
    // Cài đặt thông báo
    private boolean emailNotifications; // Nhận thông báo qua email
    private boolean smsNotifications; // Nhận thông báo qua SMS
    private boolean promotionNotifications; // Nhận thông báo khuyến mãi
}
