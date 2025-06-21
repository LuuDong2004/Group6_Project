package group6.cinema_project.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO cho response từ VietQR API hoặc service
 * Chứa thông tin QR code đã tạo
 */
@Data
public class VietQRResponse {
    
    /**
     * Trạng thái thành công
     */
    private boolean success;
    
    /**
     * Thông báo kết quả
     */
    private String message;
    
    /**
     * Mã lỗi (nếu có)
     */
    private String errorCode;
    
    /**
     * URL của QR code image (phương án 1)
     */
    private String qrCodeUrl;
    
    /**
     * QR code dưới dạng base64 (phương án 2)
     */
    private String qrCodeBase64;
    
    /**
     * Chuỗi QR data gốc
     */
    private String qrDataString;
    
    /**
     * Thông tin giao dịch
     */
    private TransactionInfo transactionInfo;
    
    /**
     * Thời gian hết hạn (timestamp)
     */
    private Long expiryTime;
    
    /**
     * Mã tham chiếu giao dịch
     */
    private String transactionRef;
    
    /**
     * Constructor mặc định
     */
    public VietQRResponse() {}
    
    /**
     * Constructor cho trường hợp thành công
     */
    public VietQRResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Constructor cho trường hợp lỗi
     */
    public VietQRResponse(boolean success, String message, String errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }
    
    /**
     * Thông tin chi tiết giao dịch
     */
    @Data
    public static class TransactionInfo {
        private String bankId;
        private String accountNo;
        private String accountName;
        private Long amount;
        private String description;
        private Integer bookingId;
    }
}
