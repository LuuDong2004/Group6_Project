package group6.cinema_project.service;

import group6.cinema_project.dto.SeatDto;

import java.util.List;

public interface ISeatService {
    List<SeatDto> getSeatsByScheduleId(Integer scheduleId);
}
