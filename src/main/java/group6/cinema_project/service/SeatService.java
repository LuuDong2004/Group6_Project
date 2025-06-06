package group6.cinema_project.service;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.SeatRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatService implements ISeatService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SeatDto> getSeatsByRoomId(Integer roomId) {
        List<Seat> seats = seatRepository.findSeatsByRoomIdOrderByRowAndNumber(roomId);
        return seats.stream()
                .map(seat -> modelMapper.map(seat, SeatDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDto> getSeatsWithStatusByRoomAndSchedule(Integer roomId, Integer scheduleId) {
        // Lấy tất cả ghế trong phòng
        List<Seat> allSeats = seatRepository.findSeatsByRoomIdOrderByRowAndNumber(roomId);

        // Lấy danh sách ghế đã đặt
        List<Seat> occupiedSeats = seatRepository.findOccupiedSeatsByRoomAndSchedule(roomId, scheduleId);
        Set<Integer> occupiedSeatIds = occupiedSeats.stream()
                .map(Seat::getId)
                .collect(Collectors.toSet());

        // Map sang DTO và set trạng thái
        return allSeats.stream()
                .map(seat -> {
                    SeatDto seatDto = modelMapper.map(seat, SeatDto.class);
                    // Set status: 1 = available, 0 = occupied
                    seatDto.setStatus(occupiedSeatIds.contains(seat.getId()) ? 0 : 1);
                    return seatDto;
                })
                .collect(Collectors.toList());
    }
}