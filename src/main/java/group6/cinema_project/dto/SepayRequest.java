package group6.cinema_project.dto;

import lombok.Data;

/**
 * DTO cho request tạo QR Sepay
 */
@Data
public class SepayRequest {
    private Long amount;
    private String orderId;
    private String description;
    private String merchantCode;
    private String callbackUrl;
    // Thêm các trường khác nếu Sepay yêu cầu
} 