package group6.cinema_project.service.impl;

import group6.cinema_project.dto.SeatReservationDto;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.entity.SeatReservation;
import group6.cinema_project.entity.Schedule;
import group6.cinema_project.entity.Ticket;
import group6.cinema_project.entity.Booking;
import group6.cinema_project.repository.ScheduleRepository;
import group6.cinema_project.repository.SeatRepository;
import group6.cinema_project.repository.SeatReservationRepository;
import group6.cinema_project.repository.TicketRepository;
import group6.cinema_project.repository.BookingRepository;
import group6.cinema_project.service.ISeatReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeatReservationService implements ISeatReservationService {
    private static final Logger logger = LoggerFactory.getLogger(SeatReservationService.class);
    private static final long PENDING_TIMEOUT_MINUTES = 15;

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private SeatReservationRepository seatReservationRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void cleanupPendingReservations() {
        try {
            logger.info("Running cleanup for pending reservations");
            Date timeoutDate = new Date(System.currentTimeMillis() - (PENDING_TIMEOUT_MINUTES * 60 * 1000));
            
            List<SeatReservation> expiredReservations = seatReservationRepository
                .findByStatusAndCreateDateBefore("PENDING", timeoutDate);
            
            for (SeatReservation reservation : expiredReservations) {
                logger.info("Removing expired pending reservation for seat {} in schedule {}", 
                    reservation.getSeat().getId(), reservation.getSchedule().getId());
                
                // Nếu có booking, cập nhật trạng thái booking thành CANCELLED
                if (reservation.getBooking() != null) {
                    Booking booking = reservation.getBooking();
                    if ("PENDING".equals(booking.getStatus())) {
                        booking.setStatus("CANCELLED");
                        bookingRepository.save(booking);
                        logger.info("Updated booking {} status to CANCELLED", booking.getId());
                    }
                }
                
                // Xóa reservation
                seatReservationRepository.delete(reservation);
            }
            
            if (!expiredReservations.isEmpty()) {
                logger.info("Cleaned up {} expired pending reservations", expiredReservations.size());
            }
        } catch (Exception e) {
            logger.error("Error cleaning up pending reservations: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<SeatReservationDto> getSeatsWithStatus(Integer scheduleId) {
        try {
            logger.info("Getting seats for schedule ID: {}", scheduleId);
            
            // Kiểm tra lịch chiếu tồn tại
            Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu với ID: " + scheduleId));
            
            // Lấy tất cả ghế trong phòng
            List<Seat> allSeats = seatRepository.findSeatsByRoomId(scheduleId);
            logger.info("Found {} seats for schedule", allSeats.size());
            
            if (allSeats.isEmpty()) {
                logger.warn("No seats found for schedule ID: {}", scheduleId);
                throw new RuntimeException("Không tìm thấy ghế cho lịch chiếu này");
            }
            
            // Lấy các ghế đã đặt
            List<SeatReservation> reservations = seatReservationRepository
                .findActiveReservationsByScheduleId(scheduleId);
            logger.info("Found {} active reservations", reservations.size());
            
            // Map trạng thái ghế
            return allSeats.stream()
                .map(seat -> {
                    SeatReservationDto dto = new SeatReservationDto();
                    dto.setSeatId(seat.getId());
                    dto.setSeatName(seat.getName());
                    dto.setRow(seat.getRow());
                    
                    // Kiểm tra trạng thái ghế
                    Optional<SeatReservation> reservation = reservations.stream()
                        .filter(r -> r.getSeat().getId().equals(seat.getId()))
                        .findFirst();
                    
                    if (reservation.isPresent()) {
                        dto.setStatus(reservation.get().getStatus());
                        dto.setCreateTime(reservation.get().getCreateDate());
                    } else {
                        dto.setStatus("AVAILABLE");
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting seats with status: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy trạng thái ghế: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean reserveSeat(Integer seatId, Integer scheduleId, Integer userId, Integer bookingId) {
        try {
            logger.info("Attempting to reserve seat {} for schedule {} by user {} for booking {}", 
                seatId, scheduleId, userId, bookingId);
            
            // Kiểm tra ghế có đang được giữ chỗ hoặc pending không
            if (seatReservationRepository.isSeatReserved(seatId, scheduleId) || 
                seatReservationRepository.isSeatPending(seatId, scheduleId)) {
                logger.warn("Seat {} is already reserved or pending for schedule {}", seatId, scheduleId);
                return false;
            }

            // Kiểm tra ghế và lịch chiếu tồn tại
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID: " + seatId));
            Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch chiếu với ID: " + scheduleId));

            // Tìm booking
            Booking booking = null;
            if (bookingId != null) {
                booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));
            }

            // Tạo bản ghi giữ chỗ mới với trạng thái PENDING
            SeatReservation reservation = new SeatReservation();
            reservation.setSeat(seat);
            reservation.setSchedule(schedule);
            reservation.setStatus("PENDING");
            reservation.setBooking(booking);
            reservation.setCreateDate(new Date());

            seatReservationRepository.save(reservation);
            logger.info("Successfully created pending reservation for seat {} for schedule {} with booking {}", seatId, scheduleId, bookingId);
            return true;
        } catch (Exception e) {
            logger.error("Error creating pending reservation: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo đặt chỗ: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public void confirmReservation(Integer seatId, Integer scheduleId, Integer ticketId) {
        try {
            logger.info("Confirming reservation for seat {} in schedule {} with ticket {}", 
                seatId, scheduleId, ticketId);
                
            SeatReservation reservation = seatReservationRepository
                .findBySeatIdAndScheduleId(seatId, scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ"));

            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

            // Cập nhật trạng thái thành RESERVED và gán vé
            reservation.setStatus("RESERVED");
            reservation.setTicket(ticket);
            seatReservationRepository.save(reservation);
            logger.info("Successfully confirmed reservation");
        } catch (Exception e) {
            logger.error("Error confirming reservation: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xác nhận đặt chỗ: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean cancelPendingReservation(Integer seatId, Integer scheduleId, Integer userId) {
        try {
            logger.info("Cancelling pending reservation for seat {} in schedule {} by user {}", 
                seatId, scheduleId, userId);
            
            // Tìm reservation PENDING
            List<SeatReservation> pendingReservations = seatReservationRepository.findAll();
            for (SeatReservation reservation : pendingReservations) {
                if (reservation.getStatus().equals("PENDING")
                        && reservation.getSeat().getId().equals(seatId)
                        && reservation.getSchedule().getId().equals(scheduleId)) {
                    
                    // Nếu có booking, cập nhật trạng thái booking thành CANCELLED
                    if (reservation.getBooking() != null) {
                        Booking booking = reservation.getBooking();
                        if ("PENDING".equals(booking.getStatus())) {
                            booking.setStatus("CANCELLED");
                            bookingRepository.save(booking);
                            logger.info("Updated booking {} status to CANCELLED", booking.getId());
                        }
                    }
                    
                    // Xóa reservation
                    seatReservationRepository.delete(reservation);
                    logger.info("Successfully cancelled pending reservation for seat {} in schedule {}", 
                        seatId, scheduleId);
                    return true;
                }
            }
            
            logger.warn("No pending reservation found for seat {} in schedule {}", seatId, scheduleId);
            return false;
        } catch (Exception e) {
            logger.error("Error cancelling pending reservation: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi hủy đặt chỗ: " + e.getMessage());
        }
    }
} 