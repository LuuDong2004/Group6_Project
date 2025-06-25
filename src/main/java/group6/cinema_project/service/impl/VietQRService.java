package group6.cinema_project.service;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.VietQRRequest;
import group6.cinema_project.dto.VietQRResponse;
import group6.cinema_project.entity.SeatReservation;
import group6.cinema_project.repository.SeatReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nayuki.qrcodegen.QrCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Service xử lý tạo QR code thanh toán VietQR
 * Hỗ trợ 2 phương án:
 * 1. Sử dụng VietQR API (img.vietqr.io)
 * 2. Tự generate QR string theo chuẩn EMVCo
 */
@Service
public class VietQRService {

    private static final Logger logger = LoggerFactory.getLogger(VietQRService.class);

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private SeatReservationRepository seatReservationRepository;

    private final WebClient webClient;

    // Lưu trữ thông tin giao dịch tạm thời (production nên dùng database)
    private final Map<String, TransactionData> transactionStore = new HashMap<>();

    public VietQRService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://img.vietqr.io")
                .build();
    }


    public VietQRResponse createQRCodeWithAPI(VietQRRequest request) {
        try {
            // Validate input
            if (!isValidRequest(request)) {
                return new VietQRResponse(false, "Thông tin đầu vào không hợp lệ", "INVALID_INPUT");
            }

            // Tạo mã tham chiếu giao dịch
            String transactionRef = generateTransactionRef();
            
            // Lưu thông tin giao dịch
            saveTransactionData(transactionRef, request);

            // Tạo URL VietQR API
            String qrUrl = buildVietQRApiUrl(request, transactionRef);

            // Tạo response
            VietQRResponse response = new VietQRResponse(true, "Tạo QR code thành công");
            response.setQrCodeUrl(qrUrl);
            response.setTransactionRef(transactionRef);
            response.setExpiryTime(System.currentTimeMillis() + (15 * 60 * 1000)); // 15 phút
            
            // Set transaction info
            VietQRResponse.TransactionInfo transactionInfo = new VietQRResponse.TransactionInfo();
            transactionInfo.setBankId(request.getBankId());
            transactionInfo.setAccountNo(request.getAccountNo());
            transactionInfo.setAccountName(request.getAccountName());
            transactionInfo.setAmount(request.getAmount());
            transactionInfo.setDescription(request.getDescription());
            transactionInfo.setBookingId(request.getBookingId());
            response.setTransactionInfo(transactionInfo);

            return response;

        } catch (Exception e) {
            return new VietQRResponse(false, "Lỗi tạo QR code: " + e.getMessage(), "SYSTEM_ERROR");
        }
    }

    /**
     * PHƯƠNG ÁN 2: Tự tạo QR code theo chuẩn EMVCo
     * Độc lập, không phụ thuộc API bên ngoài
     */
    public VietQRResponse createQRCodeWithEMVCo(VietQRRequest request) {
        try {
            // Validate input
            if (!isValidRequest(request)) {
                return new VietQRResponse(false, "Thông tin đầu vào không hợp lệ", "INVALID_INPUT");
            }

            // Tạo mã tham chiếu giao dịch
            String transactionRef = generateTransactionRef();
            
            // Lưu thông tin giao dịch
            saveTransactionData(transactionRef, request);

            // Tạo chuỗi QR theo chuẩn EMVCo
            String qrDataString = generateEMVCoQRString(request, transactionRef);

            // Tạo QR code image từ chuỗi data
            String qrCodeBase64 = generateQRCodeImage(qrDataString);

            // Tạo response
            VietQRResponse response = new VietQRResponse(true, "Tạo QR code thành công");
            response.setQrCodeBase64(qrCodeBase64);
            response.setQrDataString(qrDataString);
            response.setTransactionRef(transactionRef);
            response.setExpiryTime(System.currentTimeMillis() + (15 * 60 * 1000)); // 15 phút
            
            // Set transaction info
            VietQRResponse.TransactionInfo transactionInfo = new VietQRResponse.TransactionInfo();
            transactionInfo.setBankId(request.getBankId());
            transactionInfo.setAccountNo(request.getAccountNo());
            transactionInfo.setAccountName(request.getAccountName());
            transactionInfo.setAmount(request.getAmount());
            transactionInfo.setDescription(request.getDescription());
            transactionInfo.setBookingId(request.getBookingId());
            response.setTransactionInfo(transactionInfo);

            return response;

        } catch (Exception e) {
            return new VietQRResponse(false, "Lỗi tạo QR code: " + e.getMessage(), "SYSTEM_ERROR");
        }
    }

    /**
     * Tạo QR code cho booking cụ thể (sử dụng phương án 1)
     */
    @Transactional
    public VietQRResponse createPaymentQRForBooking(Integer bookingId, String bankId, String accountNo, String accountName) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return new VietQRResponse(false, "Không tìm thấy booking", "BOOKING_NOT_FOUND");
            }

            VietQRRequest request = new VietQRRequest();
            request.setBankId(bankId);
            request.setAccountNo(accountNo);
            request.setAccountName(accountName);
            request.setAmount((long) booking.getAmount());
            request.setDescription(String.format("Thanh toan ve xem phim - Booking %s", booking.getCode()));
            request.setBookingId(bookingId);

            return createQRCodeWithAPI(request);

        } catch (Exception e) {
            return new VietQRResponse(false, "Lỗi tạo QR thanh toán: " + e.getMessage(), "SYSTEM_ERROR");
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán
     */
    public Map<String, Object> checkPaymentStatus(String transactionRef) {
        TransactionData transaction = transactionStore.get(transactionRef);
        Map<String, Object> response = new HashMap<>();
        
        if (transaction == null) {
            response.put("success", false);
            response.put("message", "Không tìm thấy giao dịch");
            return response;
        }

        response.put("success", true);
        response.put("status", transaction.getStatus());
        response.put("bookingId", transaction.getBookingId());
        response.put("amount", transaction.getAmount());
        response.put("expiryTime", transaction.getExpiryTime());
        response.put("expired", LocalDateTime.now().isAfter(transaction.getExpiryTime()));

        return response;
    }

    /**
     * Xác nhận thanh toán thành công
     */
    @Transactional
    public Map<String, Object> confirmPayment(String transactionRef) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Confirming payment for transaction: {}", transactionRef);
            
            TransactionData transaction = transactionStore.get(transactionRef);
            if (transaction == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy giao dịch");
                return response;
            }

            if (transaction.getStatus().equals("COMPLETED")) {
                response.put("success", true);
                response.put("status", "COMPLETED");
                response.put("bookingId", transaction.getBookingId());
                response.put("amount", transaction.getAmount());
                response.put("message", "Giao dịch đã được xác nhận trước đó");
                return response;
            }

            if (LocalDateTime.now().isAfter(transaction.getExpiryTime())) {
                response.put("success", false);
                response.put("message", "Giao dịch đã hết hạn");
                return response;
            }

            // Cập nhật trạng thái giao dịch
            transaction.setStatus("COMPLETED");
            transactionStore.put(transactionRef, transaction);

            // Cập nhật trạng thái booking và seat reservation
            if (transaction.getBookingId() != null) {
                bookingService.confirmBookingPaid(transaction.getBookingId());
            }

            response.put("success", true);
            response.put("status", "COMPLETED");
            response.put("bookingId", transaction.getBookingId());
            response.put("amount", transaction.getAmount());
            response.put("message", "Thanh toán thành công");

            return response;
        } catch (Exception e) {
            logger.error("Error confirming payment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi xác nhận thanh toán: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Hủy thanh toán
     */
    @Transactional
    public Map<String, Object> cancelPayment(String transactionRef) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Cancelling payment for transaction: {}", transactionRef);
            
            TransactionData transaction = transactionStore.get(transactionRef);
            if (transaction == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy giao dịch");
                return response;
            }

            if (transaction.getStatus().equals("CANCELLED")) {
                response.put("success", true);
                response.put("status", "CANCELLED");
                response.put("bookingId", transaction.getBookingId());
                response.put("message", "Giao dịch đã được hủy trước đó");
                return response;
            }

            // Cập nhật trạng thái giao dịch
            transaction.setStatus("CANCELLED");
            transactionStore.put(transactionRef, transaction);

            // Cập nhật trạng thái booking thành CANCELLED
            if (transaction.getBookingId() != null) {
                bookingService.updateBookingStatus(transaction.getBookingId(), "CANCELLED");
                
                // Xóa các seat reservation PENDING liên quan
                List<SeatReservation> pendingReservations = seatReservationRepository.findByBookingId(transaction.getBookingId());
                for (SeatReservation reservation : pendingReservations) {
                    if ("PENDING".equals(reservation.getStatus())) {
                        seatReservationRepository.delete(reservation);
                        logger.info("Deleted pending reservation for seat {} in schedule {}", 
                            reservation.getSeat().getId(), reservation.getSchedule().getId());
                    }
                }
            }

            response.put("success", true);
            response.put("status", "CANCELLED");
            response.put("bookingId", transaction.getBookingId());
            response.put("message", "Đã hủy thanh toán thành công");

            return response;
        } catch (Exception e) {
            logger.error("Error cancelling payment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi hủy thanh toán: " + e.getMessage());
            return response;
        }
    }

    // ==================== PRIVATE METHODS ====================

    /**
     * Validate thông tin đầu vào
     */
    private boolean isValidRequest(VietQRRequest request) {
        return request != null 
            && request.getBankId() != null && !request.getBankId().trim().isEmpty()
            && request.getAccountNo() != null && !request.getAccountNo().trim().isEmpty()
            && request.getAccountName() != null && !request.getAccountName().trim().isEmpty()
            && request.getAmount() != null && request.getAmount() > 0
            && request.getDescription() != null && !request.getDescription().trim().isEmpty();
    }

    /**
     * Tạo mã tham chiếu giao dịch
     */
    private String generateTransactionRef() {
        return "VIETQR_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * Lưu thông tin giao dịch tạm thời
     */
    private void saveTransactionData(String transactionRef, VietQRRequest request) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
        TransactionData transactionData = new TransactionData(
            request.getBookingId(),
            request.getAmount(),
            expiryTime,
            "PENDING"
        );
        transactionStore.put(transactionRef, transactionData);
    }

    /**
     * Tạo URL cho VietQR API
     */
    private String buildVietQRApiUrl(VietQRRequest request, String transactionRef) {
        String description = request.getDescription() + " - Ref: " + transactionRef;

        return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
            request.getBankId(),
            request.getAccountNo(),
            request.getTemplate() != null ? request.getTemplate() : "compact",
            request.getAmount(),
            description.replace(" ", "%20"),
            request.getAccountName().replace(" ", "%20")
        );
    }

    /**
     * Tạo chuỗi QR theo chuẩn EMVCo cho VietQR
     */
    private String generateEMVCoQRString(VietQRRequest request, String transactionRef) {
        StringBuilder qrString = new StringBuilder();

        // Payload Format Indicator (ID "00")
        qrString.append("000201");

        // Point of Initiation Method (ID "01") - Static QR
        qrString.append("010212");

        // Merchant Account Information (ID "38" for VietQR)
        String merchantInfo = buildMerchantAccountInfo(request);
        qrString.append("38").append(String.format("%02d", merchantInfo.length())).append(merchantInfo);

        // Transaction Currency (ID "53") - VND = 704
        qrString.append("5303704");

        // Transaction Amount (ID "54")
        String amount = request.getAmount().toString();
        qrString.append("54").append(String.format("%02d", amount.length())).append(amount);

        // Country Code (ID "58") - VN
        qrString.append("5802VN");

        // Merchant Name (ID "59")
        String merchantName = request.getAccountName();
        if (merchantName.length() > 25) {
            merchantName = merchantName.substring(0, 25);
        }
        qrString.append("59").append(String.format("%02d", merchantName.length())).append(merchantName);

        // Additional Data Field Template (ID "62")
        String additionalData = buildAdditionalDataField(request, transactionRef);
        qrString.append("62").append(String.format("%02d", additionalData.length())).append(additionalData);

        // CRC (ID "63") - sẽ được tính và thêm vào cuối
        String qrWithoutCRC = qrString.toString() + "6304";
        String crc = calculateCRC16(qrWithoutCRC);
        qrString.append("63").append("04").append(crc);

        return qrString.toString();
    }

    /**
     * Tạo thông tin tài khoản merchant cho EMVCo
     */
    private String buildMerchantAccountInfo(VietQRRequest request) {
        StringBuilder merchantInfo = new StringBuilder();

        // GUID (ID "00") - VietQR GUID
        String guid = "A000000727";
        merchantInfo.append("00").append(String.format("%02d", guid.length())).append(guid);

        // Beneficiary Organization (ID "01") - Bank ID
        String bankId = request.getBankId();
        merchantInfo.append("01").append(String.format("%02d", bankId.length())).append(bankId);

        // Beneficiary Account (ID "02") - Account Number
        String accountNo = request.getAccountNo();
        merchantInfo.append("02").append(String.format("%02d", accountNo.length())).append(accountNo);

        return merchantInfo.toString();
    }

    /**
     * Tạo Additional Data Field cho EMVCo
     */
    private String buildAdditionalDataField(VietQRRequest request, String transactionRef) {
        StringBuilder additionalData = new StringBuilder();

        // Bill Number (ID "01")
        String billNumber = transactionRef;
        additionalData.append("01").append(String.format("%02d", billNumber.length())).append(billNumber);

        // Purpose of Transaction (ID "08")
        String purpose = request.getDescription();
        if (purpose.length() > 25) {
            purpose = purpose.substring(0, 25);
        }
        additionalData.append("08").append(String.format("%02d", purpose.length())).append(purpose);

        return additionalData.toString();
    }

    /**
     * Tính CRC16 cho chuỗi QR EMVCo
     */
    private String calculateCRC16(String data) {
        int crc = 0xFFFF;
        byte[] bytes = data.getBytes();

        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }

        return String.format("%04X", crc);
    }

    /**
     * Tạo QR code image từ chuỗi data và trả về base64
     */
    private String generateQRCodeImage(String qrData) throws IOException {
        // Tạo QR code sử dụng thư viện Nayuki
        QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);

        // Tạo BufferedImage
        int scale = 8; // Kích thước mỗi module
        int border = 4; // Viền xung quanh
        int size = (qr.size + border * 2) * scale;

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Nền trắng
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);

        // Vẽ QR code
        g2d.setColor(Color.BLACK);
        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    int px = (x + border) * scale;
                    int py = (y + border) * scale;
                    g2d.fillRect(px, py, scale, scale);
                }
            }
        }

        g2d.dispose();

        // Convert to base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    // ==================== INNER CLASSES ====================

    /**
     * Class lưu trữ thông tin giao dịch tạm thời
     */
    private static class TransactionData {
        private final Integer bookingId;
        private final Long amount;
        private final LocalDateTime expiryTime;
        private String status;

        public TransactionData(Integer bookingId, Long amount, LocalDateTime expiryTime, String status) {
            this.bookingId = bookingId;
            this.amount = amount;
            this.expiryTime = expiryTime;
            this.status = status;
        }

        public Integer getBookingId() { return bookingId; }
        public Long getAmount() { return amount; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
