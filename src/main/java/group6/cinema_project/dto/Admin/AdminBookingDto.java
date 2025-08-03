package group6.cinema_project.dto.Admin;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import group6.cinema_project.dto.BookedFoodDto;
import group6.cinema_project.dto.ScreeningScheduleDto;

/**
 * DTO cho Admin quản lý booking
 * Chứa đầy đủ thông tin cần thiết cho việc quản lý booking
 */
@Data
@Getter
@Setter
public class AdminBookingDto {
    private Integer id;
    private AdminUserDto user; // Sử dụng AdminUserDto thay vì UserDto
    private String code;
    private Integer amount;
    private String status;
    private LocalDate date;
    private Date expiryDate; // ngày hiệu lực của booking
    private String notes;
    private ScreeningScheduleDto schedule;
    private List<String> seatNames;
    private List<BookedFoodDto> foodList;
    private String voucherCode;
    
    // Thông tin quản lý cho admin
    private LocalDateTime createdDateTime; // Thời gian tạo booking chính xác
    private LocalDateTime updatedDateTime; // Thời gian cập nhật cuối
    private String createdBy; // Người tạo booking (admin hoặc customer)
    private String updatedBy; // Người cập nhật cuối
    
    // Thông tin thanh toán chi tiết
    private String paymentMethod; // Phương thức thanh toán
    private String paymentStatus; // Trạng thái thanh toán
    private String transactionId; // Mã giao dịch
    private LocalDateTime paymentDateTime; // Thời gian thanh toán
    private Double originalAmount; // Số tiền gốc trước giảm giá
    private Double discountAmount; // Số tiền giảm giá
    private Double finalAmount; // Số tiền cuối cùng
    
    // Thông tin liên hệ
    private String customerEmail;
    private String customerPhone;
    private String customerName;
    
    // Thông tin kỹ thuật
    private String ipAddress; // IP đặt vé
    private String userAgent; // Trình duyệt sử dụng
    private String deviceInfo; // Thông tin thiết bị
    
    // Thông tin xử lý
    private boolean isRefunded; // Đã hoàn tiền hay chưa
    private LocalDateTime refundDateTime; // Thời gian hoàn tiền
    private String refundReason; // Lý do hoàn tiền
    private String refundBy; // Người xử lý hoàn tiền
    
    // Ghi chú admin
    private String adminNotes; // Ghi chú của admin
    private String internalStatus; // Trạng thái nội bộ
}
