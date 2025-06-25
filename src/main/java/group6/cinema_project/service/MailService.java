package group6.cinema_project.service;

import group6.cinema_project.dto.BookingDto;

public interface MailService {
    void sendmail();
    
    /**
     * Gửi email xác nhận đặt vé
     */
    void sendBookingConfirmationEmail(BookingDto booking, String userEmail);
    
    /**
     * Gửi email vé điện tử sau khi thanh toán thành công
     */
    void sendETicketEmail(BookingDto booking, String userEmail);
    
    /**
     * Gửi email thông báo hủy vé
     */
    void sendCancellationEmail(BookingDto booking, String userEmail);
}
