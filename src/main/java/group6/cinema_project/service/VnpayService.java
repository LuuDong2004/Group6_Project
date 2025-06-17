package group6.cinema_project.service;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import group6.cinema_project.config.VnpayConfig;
import group6.cinema_project.dto.VnpayRequest;
import group6.cinema_project.dto.BookingDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class VnpayService {

    @Autowired
    private IBookingService bookingService;

    public String createPayment(VnpayRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        // Validate input
        if (paymentRequest == null) {
            throw new IllegalArgumentException("Payment request không được null");
        }

        if (paymentRequest.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID không được null");
        }

        // Get booking information to get the actual amount
        BookingDto booking;
        try {
            booking = bookingService.getBookingById(paymentRequest.getBookingId());
            if (booking == null) {
                throw new IllegalArgumentException("Không tìm thấy booking với ID: " + paymentRequest.getBookingId());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi khi lấy thông tin booking: " + e.getMessage());
        }

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "billpayment"; // Changed to billpayment for cinema tickets

        // Use actual booking amount instead of request amount
        long amount = Math.round(booking.getAmount() * 100); // Convert to VNPay format

        // Use bank code from request if provided, otherwise default
        String bankCode = (paymentRequest.getBankCode() != null && !paymentRequest.getBankCode().isEmpty())
                         ? paymentRequest.getBankCode() : "NCB";

        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnpayConfig.getIpAddress(request);
        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Improved order info format for easier extraction
        String orderInfo = String.format("Thanh toan ve xem phim - Booking ID: %d", paymentRequest.getBookingId());
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Use the standardized method from VnpayConfig
        return VnpayConfig.getPaymentUrl(vnp_Params);
    }

    public String createQRCodePayment(VnpayRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        // Validate input
        if (paymentRequest == null) {
            throw new IllegalArgumentException("Payment request không được null");
        }

        if (paymentRequest.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID không được null");
        }

        // Get booking information
        BookingDto booking = bookingService.getBookingById(paymentRequest.getBookingId());
        if (booking == null) {
            throw new IllegalArgumentException("Không tìm thấy booking với ID: " + paymentRequest.getBookingId());
        }

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "billpayment";
        long amount = Math.round(booking.getAmount() * 100);
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnpayConfig.getIpAddress(request);
        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", String.format("Thanh toan ve xem phim - Booking ID: %d", paymentRequest.getBookingId()));
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        vnp_Params.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis() + 15 * 60 * 1000)));

        // Add additional parameters for QR code
        vnp_Params.put("vnp_QRCodeType", "2"); // Type 2 for static QR code
        vnp_Params.put("vnp_QRCodeSize", "300"); // Size in pixels
        vnp_Params.put("vnp_QRCodeFormat", "image/png"); // Format of QR code image

        return VnpayConfig.getQRCodeUrl(vnp_Params);
    }

    public boolean verifyPaymentReturn(Map<String, String> vnpParams) {
        try {
            // Remove secure hash from params for verification
            Map<String, String> verifyParams = new HashMap<>(vnpParams);
            String receivedHash = verifyParams.remove("vnp_SecureHash");

            if (receivedHash == null || receivedHash.isEmpty()) {
                return false;
            }

            // Generate hash from received parameters
            String calculatedHash = VnpayConfig.hashAllFields(verifyParams);

            // Compare hashes
            return receivedHash.equalsIgnoreCase(calculatedHash);
        } catch (Exception e) {
            return false;
        }
    }

    public ResponseEntity<String> handlePaymentReturn(String responseCode) {
        if ("00".equals(responseCode)) {
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất bại! Mã lỗi: " + responseCode);
        }
    }
}
