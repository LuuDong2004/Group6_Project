package group6.cinema_project.controller;

import group6.cinema_project.dto.VietQRRequest;
import group6.cinema_project.dto.VietQRResponse;
import group6.cinema_project.service.VietQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Controller xử lý các API liên quan đến VietQR
 * Hỗ trợ tạo QR code thanh toán theo 2 phương án
 */
@RestController
@RequestMapping("/api/vietqr")
@Validated
public class VietQRController {

    @Autowired
    private VietQRService vietQRService;

    /**
     * PHƯƠNG ÁN 1: Tạo QR code sử dụng VietQR API
     * Endpoint: POST /api/vietqr/create-with-api
     */
    @PostMapping("/create-with-api")
    public ResponseEntity<VietQRResponse> createQRCodeWithAPI(@Valid @RequestBody VietQRRequest request) {
        try {
            VietQRResponse response = vietQRService.createQRCodeWithAPI(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            VietQRResponse errorResponse = new VietQRResponse(false,
                "Lỗi hệ thống: " + e.getMessage(), "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/create-with-emvco")
    public ResponseEntity<VietQRResponse> createQRCodeWithEMVCo(@Valid @RequestBody VietQRRequest request) {
        try {
            VietQRResponse response = vietQRService.createQRCodeWithEMVCo(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            VietQRResponse errorResponse = new VietQRResponse(false,
                "Lỗi hệ thống: " + e.getMessage(), "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/create-for-booking")
    public ResponseEntity<VietQRResponse> createQRForBooking(
            @RequestParam Integer bookingId,
            @RequestParam String bankId,
            @RequestParam String accountNo,
            @RequestParam String accountName) {
        try {
            VietQRResponse response = vietQRService.createPaymentQRForBooking(
                bookingId, bankId, accountNo, accountName);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            VietQRResponse errorResponse = new VietQRResponse(false,
                "Lỗi hệ thống: " + e.getMessage(), "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán
     * Endpoint: GET /api/vietqr/check-status/{transactionRef}
     */
    @GetMapping("/check-status/{transactionRef}")
    public ResponseEntity<Map<String, Object>> checkPaymentStatus(@PathVariable String transactionRef) {
        try {
            Map<String, Object> response = vietQRService.checkPaymentStatus(transactionRef);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
                ));
        }
    }

    /**
     * Xác nhận thanh toán thành công
     * Endpoint: POST /api/vietqr/confirm-payment/{transactionRef}
     */
    @PostMapping("/confirm-payment/{transactionRef}")
    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable String transactionRef) {
        try {
            Map<String, Object> response = vietQRService.confirmPayment(transactionRef);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
                ));
        }
    }


    @GetMapping("/bank-codes")
    public ResponseEntity<Map<String, Object>> getBankCodes() {
        Map<String, Object> bankCodes = Map.of(
            "success", true,
            "data", Map.of(
                "970436", "Vietcombank",
                "970415", "VietinBank",
                "970422", "MB Bank",
                "970407", "Techcombank",
                "970432", "VPBank",
                "970423", "TPBank",
                "970418", "BIDV",
                "970405", "Agribank",
                "970409", "BacABank",
                "970448", "OCB"
            )
        );

        return ResponseEntity.ok(bankCodes);
    }
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "VietQR Service",
            "timestamp", System.currentTimeMillis(),
            "message", "Service đang hoạt động bình thường"
        ));
    }
}