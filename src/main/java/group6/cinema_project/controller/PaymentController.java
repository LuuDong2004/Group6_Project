package group6.cinema_project.controller;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.VietQRResponse;
import group6.cinema_project.dto.SepayRequest;
import group6.cinema_project.dto.SepayResponse;
import group6.cinema_project.service.IBookingService;
import group6.cinema_project.service.impl.VietQRService;
import group6.cinema_project.service.impl.SepayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private IBookingService bookingService;

    @Autowired
    private VietQRService vietQRService;

    @Autowired
    private SepayService sepayService;

    @Value("${sepay.api.merchant-code}")
    private String merchantCode;

    @GetMapping
    public String showPaymentPage(@RequestParam Integer bookingId, Model model) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return "redirect:/error?message=Booking not found";
            }
            model.addAttribute("booking", booking);
            return "payment";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam Integer bookingId, 
                               @RequestParam String paymentMethod) {
        try {
            // Validate payment method
            if (!isValidPaymentMethod(paymentMethod)) {
                return "redirect:/error?message=Invalid payment method";
            }

            // Update booking status to PENDING
            bookingService.updateBookingStatus(bookingId, "PENDING");

            // Redirect based on payment method
            switch (paymentMethod.toLowerCase()) {
                case "momo":
                    return "redirect:/payment/momo/create?bookingId=" + bookingId;
                case "zalopay":
                    return "redirect:/payment/zalopay/create?bookingId=" + bookingId;
                case "vietqr":
                    return "redirect:/payment/vietqr/create?bookingId=" + bookingId;
                case "sepay":
                    return "redirect:/payment/sepay/create?bookingId=" + bookingId;
                default:
                    return "redirect:/error?message=Payment method not supported";
            }
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam Integer bookingId,
                                @RequestParam(required = false) String transactionId,
                                @RequestParam(required = false) String paymentMethod,
                                @RequestParam(required = false) String amount,
                                @RequestParam(required = false) String orderInfo,
                                @RequestParam(required = false) String bankCode,
                                @RequestParam(required = false) String bankTranNo,
                                @RequestParam(required = false) String payDate,
                                Model model) {
        try {
            // Update booking status to PAID and confirm seat reservations
            bookingService.confirmBookingPaid(bookingId);

            BookingDto booking = bookingService.getBookingById(bookingId);
            model.addAttribute("booking", booking);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("amount", amount);
            model.addAttribute("orderInfo", orderInfo);
            model.addAttribute("bankCode", bankCode);
            model.addAttribute("bankTranNo", bankTranNo);
            model.addAttribute("payDate", payDate);
            
            // Thêm thông báo về email vé điện tử
            model.addAttribute("emailSent", true);
            model.addAttribute("emailMessage", "Vé điện tử đã được gửi về email của bạn!");
            
            return "payment-success";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/failed")
    public String showFailedPage(@RequestParam(required = false) String message,
                               @RequestParam(required = false) Integer bookingId,
                               Model model) {
        try {
            // Update booking status to FAILED if bookingId is provided
            if (bookingId != null) {
                bookingService.updateBookingStatus(bookingId, "FAILED");
            }

            model.addAttribute("message", message);
            model.addAttribute("bookingId", bookingId);
            return "payment-failed";
        } catch (Exception e) {
            model.addAttribute("message", message != null ? message : "Lỗi xử lý: " + e.getMessage());
            model.addAttribute("bookingId", bookingId);
            return "payment-failed";
        }
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod != null && 
               (paymentMethod.equalsIgnoreCase("momo") ||
                paymentMethod.equalsIgnoreCase("zalopay") ||
                paymentMethod.equalsIgnoreCase("vietqr") ||
                paymentMethod.equalsIgnoreCase("sepay"));
    }

    // ==================== VIETQR ENDPOINTS ====================

    /**
     * Tạo trang thanh toán VietQR
     */
    @GetMapping("/vietqr/create")
    public String createVietQRPayment(@RequestParam Integer bookingId, Model model) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return "redirect:/payment/failed?message=Không tìm thấy booking&bookingId=" + bookingId;
            }


            String bankId = "970422"; // mb bank
            String accountNo = "0329779959"; // Số tài khoản thực tế
            String accountName = "LUU VAN DONG"; // Tên tài khoản thực tế

            // Tạo QR code thanh toán
            VietQRResponse qrResponse = vietQRService.createPaymentQRForBooking(
                bookingId, bankId, accountNo, accountName);

            if (!qrResponse.isSuccess()) {
                return "redirect:/payment/failed?message=" + qrResponse.getMessage() + "&bookingId=" + bookingId;
            }

            model.addAttribute("booking", booking);
            model.addAttribute("qrResponse", qrResponse);
            model.addAttribute("bankInfo", Map.of(
                "bankId", bankId,
                "accountNo", accountNo,
                "accountName", accountName
            ));
            model.addAttribute("accountNo", accountNo);
            model.addAttribute("accountName", accountName);
            model.addAttribute("bankId", bankId);
            model.addAttribute("amount", booking.getAmount());
            model.addAttribute("qrCodeBase64", qrResponse.getQrCodeBase64());
            model.addAttribute("qrCodeUrl", qrResponse.getQrCodeUrl());
            model.addAttribute("transactionRef", qrResponse.getTransactionRef());
            model.addAttribute("expiryTime", qrResponse.getExpiryTime());

            return "vietqr-payment";
        } catch (Exception e) {
            return "redirect:/payment/failed?message=Lỗi tạo thanh toán VietQR: " + e.getMessage() + "&bookingId=" + bookingId;
        }
    }

    /**
     * API tạo QR code VietQR cho booking
     */
    @PostMapping("/vietqr/create-qr")
    @ResponseBody
    public ResponseEntity<VietQRResponse> createVietQRCode(
            @RequestParam Integer bookingId,
            @RequestParam(defaultValue = "970436") String bankId,
            @RequestParam(defaultValue = "1234567890") String accountNo,
            @RequestParam(defaultValue = "CINEMA PROJECT") String accountName) {
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
                "Lỗi tạo QR code: " + e.getMessage(), "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán VietQR
     */
    @GetMapping("/vietqr/check-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkVietQRStatus(@RequestParam String transactionRef) {
        try {
            Map<String, Object> response = vietQRService.checkPaymentStatus(transactionRef);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi kiểm tra trạng thái: " + e.getMessage()
                ));
        }
    }

    /**
     * Xác nhận thanh toán VietQR thành công
     */
    @PostMapping("/vietqr/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmVietQRPayment(@RequestParam String transactionRef) {
        try {
            Map<String, Object> response = vietQRService.confirmPayment(transactionRef);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi xác nhận thanh toán: " + e.getMessage()
                ));
        }
    }

    /**
     * Hủy thanh toán VietQR
     */
    @PostMapping("/vietqr/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelVietQRPayment(@RequestParam String transactionRef) {
        try {
            Map<String, Object> response = vietQRService.cancelPayment(transactionRef);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Lỗi hủy thanh toán: " + e.getMessage()
                ));
        }
    }

    /**
     * Tạo QR code thanh toán Sepay
     */
    @PostMapping("/sepay/create")
    @ResponseBody
    public ResponseEntity<SepayResponse> createSepayPayment(@RequestBody SepayRequest request) {
        SepayResponse response = sepayService.createPaymentQR(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán Sepay
     */
    @GetMapping("/sepay/check-status")
    @ResponseBody
    public ResponseEntity<SepayResponse> checkSepayStatus(@RequestParam String transactionId) {
        SepayResponse response = sepayService.checkPaymentStatus(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Xác nhận thanh toán Sepay (nếu cần)
     */
    @PostMapping("/sepay/confirm")
    @ResponseBody
    public ResponseEntity<SepayResponse> confirmSepayPayment(@RequestParam String transactionId) {
        SepayResponse response = sepayService.confirmPayment(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sepay/create")
    public String createSepayPayment(@RequestParam Integer bookingId, Model model) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return "redirect:/payment/failed?message=Không tìm thấy booking&bookingId=" + bookingId;
        }
        long amount = booking.getAmount();
        SepayRequest request = new SepayRequest();
        request.setAmount(amount);
        request.setOrderId(String.valueOf(bookingId));
        request.setDescription("Thanh toán vé xem phim");
        request.setMerchantCode(merchantCode);
        request.setCallbackUrl("/payment/success?bookingId=" + bookingId);
        SepayResponse sepayResponse = sepayService.createPaymentQR(request);

        // Log debug SepayResponse
        System.out.println("Sepay response: " + sepayResponse);

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("merchantCode", merchantCode);
        model.addAttribute("qrCodeUrl", sepayResponse.getQrCodeUrl());
        model.addAttribute("qrCodeBase64", sepayResponse.getQrCodeBase64());
        Long expiryTime = sepayResponse.getExpiryTime() != null ? sepayResponse.getExpiryTime() : System.currentTimeMillis() + 15*60*1000;
        model.addAttribute("expiryTime", expiryTime);
        String transactionRef = sepayResponse.getTransactionId();
        if (transactionRef == null || transactionRef.isEmpty()) {
            transactionRef = String.valueOf(bookingId);
            System.out.println("[WARN] Sepay không trả về transactionId, dùng bookingId làm transactionRef tạm thời!");
        }
        model.addAttribute("transactionRef", transactionRef);
        // Nếu không có QR, truyền message lỗi sang template
        if ((sepayResponse.getQrCodeUrl() == null || sepayResponse.getQrCodeUrl().isEmpty()) &&
            (sepayResponse.getQrCodeBase64() == null || sepayResponse.getQrCodeBase64().isEmpty())) {
            model.addAttribute("sepayError", sepayResponse.getMessage() != null ? sepayResponse.getMessage() : "Không nhận được QR code từ Sepay");
        }
        return "sepay-payment";
    }

    @PostMapping("/api/sepay/callback")
    public ResponseEntity<String> handleSepayCallback(@RequestBody String payload) {
        System.out.println("[SEPAY CALLBACK] Payload: " + payload);

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/sepay/check-transaction")
    @ResponseBody
    public ResponseEntity<SepayResponse> checkSepayTransaction(@RequestParam String referenceNumber) {
        SepayResponse response = sepayService.checkTransactionByReference(referenceNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sepay/webhook")
    public ResponseEntity<String> handleSepayWebhook(@RequestBody String payload) {
        System.out.println("[SEPAY WEBHOOK] Payload: " + payload);

        return ResponseEntity.ok("OK");
    }
}