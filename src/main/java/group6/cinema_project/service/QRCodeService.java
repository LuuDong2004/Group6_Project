package group6.cinema_project.service;

public interface QRCodeService {
    /**
     * Tạo QR code cho mã đặt vé
     */
    String generateQRCode(String bookingCode);
    
    /**
     * Tạo QR code với thông tin chi tiết vé
     */
    String generateTicketQRCode(String bookingCode, String movieName, String seatNames);
} 