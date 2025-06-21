package group6.cinema_project.dto;

import lombok.Data;

/**
 * DTO cho response từ Sepay API
 */
@Data
public class SepayResponse {
    private boolean success;
    private String message;
    private String qrCodeUrl;
    private String qrCodeBase64;
    private String transactionId;
    private String errorCode;
    private Long expiryTime;

    // Thêm các trường khác nếu Sepay trả về
} 