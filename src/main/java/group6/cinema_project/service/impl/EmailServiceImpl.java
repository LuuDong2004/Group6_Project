package group6.cinema_project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import group6.cinema_project.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendPasswordResetEmail(String to, String resetLink, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu - MyShowz Cinema");
        message.setText(
            "Xin chào " + userName + ",\n\n" +
            "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản MyShowz Cinema.\n\n" +
            "Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu:\n" +
            resetLink + "\n\n" +
            "Liên kết này sẽ hết hạn sau 24 giờ.\n\n" +
            "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ MyShowz Cinema"
        );
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw exception to avoid breaking the flow
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendPasswordResetSuccessEmail(String to, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mật khẩu đã được đặt lại thành công - MyShowz Cinema");
        message.setText(
            "Xin chào " + userName + ",\n\n" +
            "Mật khẩu của bạn đã được đặt lại thành công.\n\n" +
            "Nếu bạn không thực hiện thao tác này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ MyShowz Cinema"
        );
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending success email: " + e.getMessage());
        }
    }
    
    @Override
    public void sendAdminPasswordResetEmail(String to, String userName, String newPassword, String adminName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mật khẩu đã được đặt lại bởi Admin - MyShowz Cinema");
        message.setText(
            "Xin chào " + userName + ",\n\n" +
            "Mật khẩu tài khoản của bạn đã được đặt lại bởi quản trị viên: " + adminName + "\n\n" +
            "Mật khẩu mới của bạn là: " + newPassword + "\n\n" +
            "Vui lòng đăng nhập và thay đổi mật khẩu ngay lập tức để đảm bảo an toàn.\n\n" +
            "Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ MyShowz Cinema"
        );
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending admin reset email: " + e.getMessage());
        }
    }
} 