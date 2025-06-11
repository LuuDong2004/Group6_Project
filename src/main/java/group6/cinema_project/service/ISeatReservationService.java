package group6.cinema_project.service;

import group6.cinema_project.dto.SeatReservationDto;
import java.util.List;

public interface ISeatReservationService {
    /**
     * Lấy danh sách ghế với trạng thái cho một lịch chiếu
     * @param scheduleId ID của lịch chiếu
     * @return Danh sách ghế với trạng thái
     */
    List<SeatReservationDto> getSeatsWithStatus(Integer scheduleId);

    /**
     * Đặt ghế cho người dùng
     * @param seatId ID của ghế
     * @param scheduleId ID của lịch chiếu
     * @param userId ID của người dùng
     * @return true nếu đặt ghế thành công, false nếu ghế đã được đặt
     */
    boolean reserveSeat(Integer seatId, Integer scheduleId, Integer userId);

    /**
     * Xác nhận đặt chỗ sau khi thanh toán
     * @param seatId ID của ghế
     * @param scheduleId ID của lịch chiếu
     * @param ticketId ID của vé
     */
    void confirmReservation(Integer seatId, Integer scheduleId, Integer ticketId);
} 