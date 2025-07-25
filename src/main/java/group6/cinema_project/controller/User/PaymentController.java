package group6.cinema_project.controller.User;


import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.TransactionSepayDto;
import group6.cinema_project.service.User.IBookingService;
import group6.cinema_project.service.User.IPaymentService;
import group6.cinema_project.service.User.IVoucherService;

import group6.cinema_project.service.User.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import group6.cinema_project.entity.Qa.Voucher;


@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${SEPAY_WEBHOOK_APIKEY:default_key}")
    private String sepaySecretKey;

    @Value("${domain}")
    private String domain;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IBookingService bookingService;
    @Autowired
    private MailService mailService;
    @Autowired
    private IVoucherService voucherService;


    @GetMapping
    public String showPaymentPage(@RequestParam Integer bookingId,
                                  @RequestParam(required = false) String voucherCode,
                                  Model model) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return "redirect:/error?message=Booking not found";
            }
            double originalAmount = booking.getAmount();
            double discount = 0;
            String voucherMessage = null;
            if (voucherCode != null && !voucherCode.isBlank()) {
                Voucher voucher = voucherService.validateVoucher(voucherCode);
                if (voucher != null) {
                    if (voucher.getDiscountPercent() > 0) {
                        discount = originalAmount * voucher.getDiscountPercent() / 100;
                    } else if (voucher.getDiscountAmount() > 0) {
                        discount = voucher.getDiscountAmount();
                    }
                    voucherMessage = "Áp dụng mã thành công! Giảm: " + String.format("%,.0f", discount) + " VND";
                } else {
                    voucherMessage = "Mã không hợp lệ hoặc đã hết hạn.";
                }
            }
            double finalAmount = Math.max(0, originalAmount - discount);
            model.addAttribute("booking", booking);
            model.addAttribute("voucherCode", voucherCode);
            model.addAttribute("voucherMessage", voucherMessage);
            model.addAttribute("finalAmount", finalAmount);
            return "payment";
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }
    @GetMapping("/process")
    public String createQrCode(@RequestParam Integer bookingId, Model model) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            model.addAttribute("error", "Không tìm thấy booking!");
            return "error";
        }
        // Tạo transactionId
        String transactionId = String.valueOf(System.currentTimeMillis());
        TransactionSepayDto dto = new TransactionSepayDto();
        dto.setTransactionId(transactionId);
        dto.setAmount(booking.getAmount());
        dto.setMerchantCode("0329779959");
        dto.setDescription("Thanh toán vé xem phim");
        dto.setStatus("PENDING");
        dto.setBookingId(bookingId);

        Map<String, Object> result = paymentService.createSepayTransaction(dto);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("amount", booking.getAmount());
        model.addAttribute("transactionId", transactionId);
        model.addAttribute("stk", dto.getMerchantCode());
        model.addAttribute("qrCodeUrl", result.get("qrCodeUrl"));
        return "sepay-payment";
    }

    @PostMapping("/process")
    public String createQrCodePost(@RequestParam Integer bookingId,
                                   @RequestParam(required = false) String voucherCode,
                                   Model model) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        double originalAmount = booking.getAmount();
        double discount = 0;
        if (voucherCode != null && !voucherCode.isBlank()) {
            Voucher voucher = voucherService.validateVoucher(voucherCode);
            if (voucher != null) {
                if (voucher.getDiscountPercent() > 0) {
                    discount = originalAmount * voucher.getDiscountPercent() / 100;
                } else if (voucher.getDiscountAmount() > 0) {
                    discount = voucher.getDiscountAmount();
                }
            }
        }
        double finalAmount = Math.max(0, originalAmount - discount);
        bookingService.updateBookingAmount(bookingId, finalAmount);
        // Lấy lại booking từ DB để đảm bảo amount đã cập nhật
        BookingDto updatedBooking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", updatedBooking);
        return createQrCode(bookingId, model);
    }

    @PostMapping("/apply-voucher")
    @ResponseBody
    public Map<String, Object> applyVoucher(@RequestParam String code) {
        Voucher voucher = voucherService.validateVoucher(code);
        if (voucher != null) {
            return Map.of(
                "success", true,
                "discountAmount", voucher.getDiscountAmount(),
                "discountPercent", voucher.getDiscountPercent(),
                "message", "Áp dụng mã thành công!"
            );
        } else {
            return Map.of(
                "success", false,
                "message", "Mã không hợp lệ hoặc đã hết hạn."
            );
        }
    }

    @PostMapping("/sepay/webhook")
    @ResponseBody
    public String verifyWebhook(@RequestBody Map<String, Object> payload) {
        Object contentObj = payload.get("content");
        if (contentObj == null) {
            throw new IllegalArgumentException("Content field is missing in webhook payload");
        }
        String content = contentObj.toString();
        String regex = "[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        String transactionCode = null;
        if (matcher.find()) {
            transactionCode = matcher.group();
        } else {
            throw new IllegalArgumentException("Not Found transaction code");
        }
        paymentService.handleSepayWebhook(transactionCode);
        return "OK";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Integer bookingId,
                                 @RequestParam(required = false) String transactionId,
                                 @RequestParam(required = false) String paymentMethod,
                                 @RequestParam(required = false) Double amount,
                                 Model model) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("transactionId", transactionId);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("amount", amount);
        if (booking.getVoucherCode() != null && !booking.getVoucherCode().isBlank()) {
            voucherService.markVoucherUsed(1L);
        }
        return "payment-success";
    }

    @GetMapping("/failed")
    public String paymentFailed(@RequestParam Integer bookingId,
                                @RequestParam(required = false) String message,
                                Model model) {
        BookingDto booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("message", message);
        return "payment-failed";
    }

    @PostMapping("/sepay/cancel")
    @ResponseBody
    public Map<String, Object> cancelSepayPayment(@RequestParam String transactionId) {
        try {
            Integer bookingId = paymentService.cancelSepayTransaction(transactionId);
            return Map.of("success", true, "bookingId", bookingId);
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}