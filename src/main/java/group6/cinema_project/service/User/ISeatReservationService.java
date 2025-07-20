package group6.cinema_project.service.User;

import group6.cinema_project.dto.SeatReservationDto;
import java.util.List;

public interface ISeatReservationService {

    List<SeatReservationDto> getSeatsWithStatus(Integer scheduleId);

    boolean reserveSeat(Integer seatId, Integer scheduleId, Integer userId, Integer bookingId);

    void confirmReservation(Integer seatId, Integer scheduleId, Integer ticketId);
    
    boolean cancelPendingReservation(Integer seatId, Integer scheduleId, Integer userId);
} 