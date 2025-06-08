package group6.cinema_project.service;

import group6.cinema_project.dto.SeatDto;

import java.util.List;

public interface ISeatService {
    List<SeatDto> getSeatsByRoomId(Integer roomId);

    // Thêm method mới để lấy ghế với trạng thái đã đặt/chưa đặt
    List<SeatDto> getSeatsWithStatusByRoomAndSchedule(Integer roomId, Integer scheduleId);
}