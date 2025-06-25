package group6.cinema_project.service.impl;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.MailService;
import group6.cinema_project.service.QRCodeService;
import group6.cinema_project.service.SendGridMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private SendGridMailService sendGridMailService;

    @Value("${spring.mail.username:your-email@example.com}")
    private String fromEmail;

    private String formatDate(java.util.Date date, String pattern) {
        if (date == null) return "";
        
        if (date instanceof Date) {
            // Xử lý java.sql.Date
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
    public void sendmail() {
        // Không dùng
    }

    @Override
    public void sendBookingConfirmationEmail(BookingDto booking, String userEmail) {
        try {
            String subject = "Xác nhận đặt vé - " + booking.getSchedule().getMovie().getName();
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("bookingCode", booking.getCode());
            context.setVariable("movieName", booking.getSchedule().getMovie().getName());
            context.setVariable("cinemaName", booking.getSchedule().getBranch().getName());
            context.setVariable("roomName", booking.getSchedule().getScreeningRoom().getName());
            context.setVariable("screeningDate", formatDate(booking.getSchedule().getScreeningDate(), "dd/MM/yyyy"));
            context.setVariable("startTime", formatTime(booking.getSchedule().getStartTime(), "HH:mm"));
            context.setVariable("totalAmount", formatAmount(booking.getAmount()));
            context.setVariable("seatNames", booking.getSeatNames());
            String htmlContent = templateEngine.process("email/booking-confirmation", context);
            sendGridMailService.sendEmail(userEmail, subject, htmlContent);
            System.out.println("Email xác nhận đặt vé đã được gửi đến: " + userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email xác nhận đặt vé: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email xác nhận đặt vé", e);
        }
    }

    @Override
    public void sendETicketEmail(BookingDto booking, String userEmail) {
        try {
            String subject = "Vé điện tử - " + booking.getSchedule().getMovie().getName();
            String seatNamesStr = String.join(", ", booking.getSeatNames());
            String qrCode = qrCodeService.generateTicketQRCode(
                booking.getCode(), 
                booking.getSchedule().getMovie().getName(), 
                seatNamesStr
            );
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("bookingCode", booking.getCode());
            context.setVariable("movieName", booking.getSchedule().getMovie().getName());
            context.setVariable("cinemaName", booking.getSchedule().getBranch().getName());
            context.setVariable("roomName", booking.getSchedule().getScreeningRoom().getName());
            context.setVariable("screeningDate", formatDate(booking.getSchedule().getScreeningDate(), "dd/MM/yyyy"));
            context.setVariable("startTime", formatTime(booking.getSchedule().getStartTime(), "HH:mm"));
            context.setVariable("totalAmount", formatAmount(booking.getAmount()));
            context.setVariable("seatNames", booking.getSeatNames());
            context.setVariable("qrCode", qrCode);
            String htmlContent = templateEngine.process("email/e-ticket", context);
            sendGridMailService.sendEmail(userEmail, subject, htmlContent);
            System.out.println("Email vé điện tử đã được gửi đến: " + userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email vé điện tử: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email vé điện tử", e);
        }
    }

    @Override
    public void sendCancellationEmail(BookingDto booking, String userEmail) {
        try {
            String subject = "Thông báo hủy vé - " + booking.getSchedule().getMovie().getName();
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("bookingCode", booking.getCode());
            context.setVariable("movieName", booking.getSchedule().getMovie().getName());
            context.setVariable("screeningDate", formatDate(booking.getSchedule().getScreeningDate(), "dd/MM/yyyy"));
            context.setVariable("startTime", formatTime(booking.getSchedule().getStartTime(), "HH:mm"));
            context.setVariable("totalAmount", formatAmount(booking.getAmount()));
            String htmlContent = templateEngine.process("email/booking-cancellation", context);
            sendGridMailService.sendEmail(userEmail, subject, htmlContent);
            System.out.println("Email thông báo hủy vé đã được gửi đến: " + userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi gửi email thông báo hủy vé: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email thông báo hủy vé", e);
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
} 