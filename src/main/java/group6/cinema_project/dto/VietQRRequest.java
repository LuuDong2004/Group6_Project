package group6.cinema_project.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO cho request tạo QR code VietQR
 * Chứa thông tin cần thiết để tạo mã QR thanh toán
 */
@Data
public class VietQRRequest {
    
    /**
     * Mã ngân hàng (BIN) - VD: 970436 cho Vietcombank
     */
    @NotBlank(message = "Mã ngân hàng không được để trống")
    private String bankId;
    
    /**
     * Số tài khoản ngân hàng
     */
    @NotBlank(message = "Số tài khoản không được để trống")
    private String accountNo;
    
    /**
     * Tên chủ tài khoản
     */
    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String accountName;
    
    /**
     * Số tiền cần thanh toán
     */
    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private Long amount;
    
    /**
     * Nội dung chuyển khoản
     */
    @NotBlank(message = "Nội dung chuyển khoản không được để trống")
    private String description;
    
    /**
     * Template sử dụng (compact, print, qr_only)
     */
    private String template = "compact";
    
    /**
     * ID booking liên quan (optional)
     */
    private Integer bookingId;
    
    /**
     * Constructor mặc định
     */
    public VietQRRequest() {}
    
    /**
     * Constructor với các tham số cơ bản
     */
    public VietQRRequest(String bankId, String accountNo, String accountName, 
                        Long amount, String description) {
        this.bankId = bankId;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.amount = amount;
        this.description = description;
    }
}
