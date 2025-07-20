package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.User.MailService;
import group6.cinema_project.service.User.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Autowired
    private JavaMailSender mailSender;



    private String formatDate(java.util.Date date, String pattern) {
        if (date == null) return "";
        
        if (date instanceof Date) {

            Date sqlDate = (Date) date;
            LocalDate localDate = sqlDate.toLocalDate();
            return localDate.format(DateTimeFormatter.ofPattern(pattern));
        } else {
            // Xử lý java.util.Date
            return date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern(pattern));
        }
    }

    private String formatTime(java.util.Date time, String pattern) {
        if (time == null) return "";
        
        if (time instanceof Time) {
            // Xử lý java.sql.Time
            Time sqlTime = (Time) time;
            LocalTime localTime = sqlTime.toLocalTime();
            return localTime.format(DateTimeFormatter.ofPattern(pattern));
        } else {
            // Xử lý java.util.Date cho thời gian
            return time.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern(pattern));
        }
    }




    @Override
    public void sendETicketEmail(BookingDto booking, String userEmail) {
        System.out.println("[DEBUG] Gửi mail vé điện tử tới: " + userEmail); // Thêm log debug
        try {
            String subject = "Vé điện tử - " + booking.getSchedule().getMovie().getName();
            String seatNamesStr = String.join(", ", booking.getSeatNames());
            // Lấy dữ liệu QR code dạng file
            byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(booking.getCode());
            String qrBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("bookingCode", booking.getCode());
            context.setVariable("movieName", booking.getSchedule().getMovie().getName());
            context.setVariable("cinemaName", booking.getSchedule().getBranch().getName());
            context.setVariable("roomName", booking.getSchedule().getScreeningRoom().getName());
            context.setVariable("screeningDate", booking.getSchedule().getScreeningDate() != null ? booking.getSchedule().getScreeningDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            context.setVariable("startTime", booking.getSchedule().getStartTime() != null ? booking.getSchedule().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
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
            return String.format("%,.0f VNĐ", ((Number)amount).doubleValue());
        } else if (amount instanceof Long || amount instanceof Integer) {
            return String.format("%,d VNĐ", ((Number)amount).longValue());
        } else {
            return amount + " VNĐ";
        }
    }

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