package group6.cinema_project.service.impl;


import group6.cinema_project.dto.TransactionSepayDto;

import group6.cinema_project.entity.SeatReservation;
import group6.cinema_project.repository.BookingRepository;
import group6.cinema_project.repository.SeatReservationRepository;
import group6.cinema_project.service.IBookingService;
import group6.cinema_project.service.IPaymentService;


import group6.cinema_project.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import group6.cinema_project.entity.TransactionSepay;
import group6.cinema_project.entity.Booking;
import group6.cinema_project.repository.SepayRepository;
import group6.cinema_project.dto.BookingDto;


import java.time.LocalDate;
import java.util.Map;
import java.util.List;


@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {



    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private SepayRepository sepayRepository;
    @Autowired
    private SeatReservationRepository seatReservationRepository;
    @Autowired
    private PaymentStatusWebSocketService paymentStatusWebSocketService;
    @Autowired
    private IBookingService bookingService;



    @Override
    public void handleSepayWebhook(String transactionId) {
        TransactionSepay transactionSepay = sepayRepository.findByTransactionId(transactionId);
        if (transactionSepay == null) {
            System.err.println("Transaction not found for id: " + transactionId);
            throw new IllegalArgumentException("Transaction not found for id: " + transactionId);
        }
        transactionSepay.setStatus("COMPLETED");

        sepayRepository.save(transactionSepay);

        Booking booking = transactionSepay.getBooking();
        if (booking == null) {
            System.err.println("Booking is null for transaction: " + transactionSepay.getTransactionId());
            return;
        }
        if (transactionSepay.getStatus().equalsIgnoreCase("COMPLETED")) {
            booking.setStatus("PAID");
            bookingRepository.save(booking);

            // Cập nhật trạng thái các SeatReservation liên quan
            List<SeatReservation> reservations = seatReservationRepository.findByBookingId(booking.getId());
            for (SeatReservation reservation : reservations) {
                if ("PENDING".equalsIgnoreCase(reservation.getStatus())) {
                    reservation.setStatus("RESERVED");
                    seatReservationRepository.save(reservation);
                }
            }
            // Gửi trạng thái qua WebSocket
            paymentStatusWebSocketService.sendPaymentStatus(
                transactionSepay.getTransactionId(),
                "COMPLETED",
                booking.getId(),
                transactionSepay.getAmount()
            );
            // Gửi email vé điện tử
            try {
                BookingDto bookingDto = bookingService.getBookingById(booking.getId());
                String email = booking.getUser() != null ? booking.getUser().getEmail() : null;
                if (bookingDto != null && email != null) {
                    mailService.sendETicketEmail(bookingDto, email);
                } else {
                    System.err.println("Không thể gửi email: thiếu thông tin bookingDto hoặc email");
                }
            } catch (Exception e) {
                System.err.println("Error sending e-ticket email: " + e.getMessage());
            }

        } else if (transactionSepay.getStatus().equalsIgnoreCase("CANCELLED")) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            // Có thể cập nhật trạng thái ghế về AVAILABLE hoặc xoá reservation nếu cần
            List<SeatReservation> reservations = seatReservationRepository.findByBookingId(booking.getId());
            for (SeatReservation reservation : reservations) {
                if ("PENDING".equalsIgnoreCase(reservation.getStatus())) {
                    seatReservationRepository.delete(reservation);
                }
            }
        }

    }

    @Override
    public Map<String, Object> createSepayTransaction(TransactionSepayDto dto){
            TransactionSepay transaction = new TransactionSepay();
            transaction.setTransactionId(dto.getTransactionId());
            transaction.setAmount(dto.getAmount());
            transaction.setMerchantCode(dto.getMerchantCode());
            transaction.setDescription(dto.getDescription());
            transaction.setStatus(dto.getStatus());
            transaction.setCreatedAt(LocalDate.now());
            transaction.setUpdatedAt(LocalDate.now());
            // Lấy booking từ DB và set vào transaction
            if (dto.getBookingId() != null) {
                Booking booking = bookingRepository.findById(dto.getBookingId()).orElse(null);
                transaction.setBooking(booking);
            }
            sepayRepository.save(transaction);

        // Sinh QR code Sepay
        String acc = "0329779959";
        String bank = "MB";
        String amountStr = String.valueOf((int) dto.getAmount());
        String des = "Thanh toan ve xem phim #" + dto.getTransactionId();
        String qrCodeUrl = "https://qr.sepay.vn/img?acc=" + acc + "&bank=" + bank + "&amount=" + amountStr + "&des=" + java.net.URLEncoder.encode(des, java.nio.charset.StandardCharsets.UTF_8);

        return Map.of(
                "transactionId", dto.getTransactionId(),
                "qrCodeUrl", qrCodeUrl
        );

    }

    @Override
    public Integer cancelSepayTransaction(String transactionId) {
        TransactionSepay transaction = sepayRepository.findByTransactionId(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found for id: " + transactionId);
        }
        transaction.setStatus("CANCELLED");
        sepayRepository.save(transaction);

        Booking booking = transaction.getBooking();
        if (booking != null) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            // Xóa hoặc cập nhật trạng thái các SeatReservation liên quan
            List<SeatReservation> reservations = seatReservationRepository.findByBookingId(booking.getId());
            for (SeatReservation reservation : reservations) {
                if ("PENDING".equalsIgnoreCase(reservation.getStatus())) {
                    seatReservationRepository.delete(reservation);
                }
            }
            // Gửi WebSocket thông báo hủy
            paymentStatusWebSocketService.sendPaymentStatus(
                transaction.getTransactionId(),
                "CANCELLED",
                booking.getId(),
                transaction.getAmount()
            );
            return booking.getId();
        }
        return null;
    }

}