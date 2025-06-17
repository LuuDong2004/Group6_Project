package group6.cinema_project.controller;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.VnpayRequest;
import group6.cinema_project.service.IBookingService;
import group6.cinema_project.service.VnpayService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private IBookingService bookingService;

    @Autowired
    private VnpayService vnpayService;

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
                case "vnpay":
                    return "redirect:/payment/vnpay/create?bookingId=" + bookingId;
                case "momo":
                    return "redirect:/payment/momo/create?bookingId=" + bookingId;
                case "zalopay":
                    return "redirect:/payment/zalopay/create?bookingId=" + bookingId;
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
            // Update booking status to PAID
            bookingService.updateBookingStatus(bookingId,"PAID");

            BookingDto booking = bookingService.getBookingById(bookingId);
            model.addAttribute("booking", booking);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("amount", amount);
            model.addAttribute("orderInfo", orderInfo);
            model.addAttribute("bankCode", bankCode);
            model.addAttribute("bankTranNo", bankTranNo);
            model.addAttribute("payDate", payDate);
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

    @PostMapping("/vnpay/create-payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody VnpayRequest vnpayRequest,
                                                           HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Validate request
            if (vnpayRequest.getBookingId() == null) {
                result.put("error", "Booking ID không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }

            String paymentUrl = vnpayService.createPayment(vnpayRequest, request);
            result.put("paymentUrl", paymentUrl);
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            result.put("error", "Đã xảy ra lỗi khi tạo thanh toán: " + e.getMessage());
            result.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/vnpay/create-qr")
    @ResponseBody
    public ResponseEntity<?> createQRCodePayment(@RequestBody VnpayRequest paymentRequest, HttpServletRequest request) {
        try {
            String qrCodeUrl = vnpayService.createQRCodePayment(paymentRequest, request);
            return ResponseEntity.ok(Map.of("qrCodeUrl", qrCodeUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/vnpay/return")
    public String handleVnpayReturn(HttpServletRequest request) {
        try {
            // Get all parameters from request
            Map<String, String> vnpParams = new HashMap<>();
            for (String paramName : request.getParameterMap().keySet()) {
                String paramValue = request.getParameter(paramName);
                if (paramValue != null && !paramValue.isEmpty()) {
                    vnpParams.put(paramName, paramValue);
                }
            }

            // Verify signature first
            if (!vnpayService.verifyPaymentReturn(vnpParams)) {
                return "redirect:/payment/failed?message=Chữ ký không hợp lệ";
            }

            String responseCode = vnpParams.get("vnp_ResponseCode");
            String txnRef = vnpParams.get("vnp_TxnRef");
            String amount = vnpParams.get("vnp_Amount");
            String orderInfo = vnpParams.get("vnp_OrderInfo");
            String bankCode = vnpParams.get("vnp_BankCode");
            String bankTranNo = vnpParams.get("vnp_BankTranNo");
            String payDate = vnpParams.get("vnp_PayDate");

            if ("00".equals(responseCode)) {
                // Extract booking ID from orderInfo
                Integer bookingId = extractBookingId(orderInfo);
                return "redirect:/payment/success?bookingId=" + bookingId +
                       "&transactionId=" + txnRef +
                       "&paymentMethod=vnpay" +
                       "&amount=" + amount +
                       "&orderInfo=" + orderInfo +
                       "&bankCode=" + bankCode +
                       "&bankTranNo=" + bankTranNo +
                       "&payDate=" + payDate;
            } else {
                // Get error message based on response code
                String errorMessage = getVnpayErrorMessage(responseCode);
                return "redirect:/payment/failed?message=" + errorMessage + " (Mã lỗi: " + responseCode + ")";
            }
        } catch (Exception e) {
            return "redirect:/payment/failed?message=Lỗi xử lý kết quả thanh toán: " + e.getMessage();
        }
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod != null && 
               (paymentMethod.equalsIgnoreCase("vnpay") ||
                paymentMethod.equalsIgnoreCase("momo") ||
                paymentMethod.equalsIgnoreCase("zalopay"));
    }

    private Integer extractBookingId(String orderInfo) {
        // Extract booking ID from orderInfo
        // Format: "Thanh toan ve xem phim - Booking ID: {bookingId}"
        try {
            String[] parts = orderInfo.split("Booking ID:");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1].trim());
            }
            throw new IllegalArgumentException("Không tìm thấy Booking ID trong order info");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Booking ID không hợp lệ");
        } catch (Exception e) {
            throw new IllegalArgumentException("Định dạng order info không hợp lệ: " + orderInfo);
        }
    }

    private String getVnpayErrorMessage(String responseCode) {
        switch (responseCode) {
            case "01": return "Giao dịch chưa hoàn tất";
            case "02": return "Giao dịch bị lỗi";
            case "04": return "Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)";
            case "05": return "VNPAY đang xử lý giao dịch này (GD hoàn tiền)";
            case "06": return "VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)";
            case "07": return "Giao dịch bị nghi ngờ gian lận";
            case "09": return "GD Hoàn trả bị từ chối";
            case "10": return "Đã giao hàng";
            case "20": return "Đã thu tiền khách hàng";
            case "21": return "Giao dịch chưa được thanh toán";
            case "22": return "Giao dịch bị hủy";
            case "24": return "Khách hàng hủy giao dịch";
            default: return "Giao dịch thất bại";
        }
    }
}