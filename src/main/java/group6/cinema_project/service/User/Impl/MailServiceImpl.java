package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.User.MailService;
import group6.cinema_project.service.User.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private SendGridMailService sendGridMailService;

    @Override
    public void sendETicketEmail(BookingDto booking, String userEmail) {
        System.out.println("[DEBUG] Gửi mail vé điện tử tới: " + userEmail); // Thêm log debug
        try {
            String subject = "Vé điện tử - " + booking.getSchedule().getMovie().getName();
            // Lấy dữ liệu QR code dạng file
            byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(booking.getCode());
            String qrBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("bookingCode", booking.getCode());
            context.setVariable("movieName", booking.getSchedule().getMovie().getName());
            context.setVariable("cinemaName", booking.getSchedule().getBranch().getName());
            context.setVariable("roomName", booking.getSchedule().getScreeningRoom().getName());
            context.setVariable("screeningDate",
                    booking.getSchedule().getScreeningDate() != null
                            ? booking.getSchedule().getScreeningDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "");
            context.setVariable("startTime",
                    booking.getSchedule().getStartTime() != null
                            ? booking.getSchedule().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                            : "");
            context.setVariable("totalAmount", formatAmount(booking.getAmount()));
            context.setVariable("seatNames", booking.getSeatNames());
            // Thêm danh sách food vào context
            context.setVariable("foodList", booking.getFoodList());
            // Tạo QR cho từng món ăn
            List<Map<String, Object>> foodWithQr = new ArrayList<>();
            if (booking.getFoodList() != null) {
                for (var food : booking.getFoodList()) {
                    String qrData = "BOOKING:" + booking.getCode() + "|FOOD:" + food.getName();
                    byte[] qrBytes = qrCodeService.generateQRCodeBytes(qrData);
                    String foodQrBase64 = Base64.getEncoder().encodeToString(qrBytes);
                    Map<String, Object> foodMap = new HashMap<>();
                    foodMap.put("name", food.getName());
                    foodMap.put("quantity", food.getQuantity());
                    foodMap.put("qrBase64", foodQrBase64);
                    foodWithQr.add(foodMap);
                }
            }
            context.setVariable("foodWithQr", foodWithQr);
            context.setVariable("qrBase64", qrBase64);
            String htmlContent = templateEngine.process("email/e-ticket", context);
            // Gửi email KHÔNG đính kèm file QR code, chỉ gửi HTML
            sendGridMailService.sendEmail(userEmail, subject, htmlContent);
            System.out.println("Email vé điện tử đã được gửi đến: " + userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email vé điện tử: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email vé điện tử", e);
        }
    }

    private String formatAmount(Object amount) {
        if (amount instanceof Double || amount instanceof Float) {
            return String.format("%,.0f VNĐ", ((Number) amount).doubleValue());
        } else if (amount instanceof Long || amount instanceof Integer) {
            return String.format("%,d VNĐ", ((Number) amount).longValue());
        } else {
            return amount + " VNĐ";
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink, String userName) {
        String subject = "Đặt lại mật khẩu - MyShowz Cinema";
        String htmlContent = "<p>Xin chào <b>" + userName + "</b>,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản MyShowz Cinema.</p>"
                + "<p>Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu:<br>"
                + "<a href='" + resetLink + "'>" + resetLink + "</a></p>"
                + "<p>Liên kết này sẽ hết hạn sau 24 giờ.</p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "<p>Trân trọng,<br>Đội ngũ MyShowz Cinema</p>";
        try {
            sendGridMailService.sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetSuccessEmail(String to, String userName) {
        String subject = "Mật khẩu đã được đặt lại thành công - MyShowz Cinema";
        String htmlContent = "<p>Xin chào <b>" + userName + "</b>,</p>"
                + "<p>Mật khẩu của bạn đã được đặt lại thành công.</p>"
                + "<p>Nếu bạn không thực hiện thao tác này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>"
                + "<p>Trân trọng,<br>Đội ngũ MyShowz Cinema</p>";
        try {
            sendGridMailService.sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Error sending success email: " + e.getMessage());
        }
    }

    @Override
    public void sendAdminPasswordResetEmail(String to, String userName, String newPassword, String adminName) {
        String subject = "Mật khẩu đã được đặt lại bởi Admin - MyShowz Cinema";
        String htmlContent = "<p>Xin chào <b>" + userName + "</b>,</p>"
                + "<p>Mật khẩu tài khoản của bạn đã được đặt lại bởi quản trị viên: <b>" + adminName + "</b></p>"
                + "<p>Mật khẩu mới của bạn là: <b>" + newPassword + "</b></p>"
                + "<p>Vui lòng đăng nhập và thay đổi mật khẩu ngay lập tức để đảm bảo an toàn.</p>"
                + "<p>Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>"
                + "<p>Trân trọng,<br>Đội ngũ MyShowz Cinema</p>";
        try {
            sendGridMailService.sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Error sending admin reset email: " + e.getMessage());
        }
    }
}