package group6.cinema_project.service.User;

public interface QRCodeService {
    byte[] generateQRCodeBytes(String bookingCode);
} 