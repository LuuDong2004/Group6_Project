package group6.cinema_project.service.impl;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.MailService;
import group6.cinema_project.service.QRCodeService;
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

    @Value("${spring.mail.username}")
    private String fromEmail;

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
        try {
            String subject = "Vé điện tử - " + booking.getSchedule().getMovie().getName();
            String seatNamesStr = String.join(", ", booking.getSeatNames());
            // Lấy dữ liệu QR code dạng file
            byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(booking.getCode());
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
            // Không cần context.setVariable("qrCode", ...);
            String htmlContent = templateEngine.process("email/e-ticket", context);
            // Gửi email với file đính kèm QR code dạng inline
            sendGridMailService.sendEmail(userEmail, subject, htmlContent, qrCodeBytes, "qr-code.png", "image/png");
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
} 