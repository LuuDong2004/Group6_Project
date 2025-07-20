package group6.cinema_project.service.User;

import group6.cinema_project.dto.BookingDto;

public interface MailService {
    void sendETicketEmail(BookingDto booking, String userEmail);
    void sendPasswordResetEmail(String to, String resetLink, String userName);

    void sendPasswordResetSuccessEmail(String to, String userName);

    void sendAdminPasswordResetEmail(String to, String userName, String newPassword, String adminName);
}
