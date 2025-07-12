package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.ScreeningRoomDto;

public interface IScreeningRoomService {
    List<ScreeningRoomDto> getAllRooms();
    ScreeningRoomDto getRoomById(int id);
    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
    boolean deleteRoom(int id);
} 