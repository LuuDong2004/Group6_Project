package group6.cinema_project.service;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.ScreeningRoomDto;

public interface ScreeningRoomService {
    ScreeningRoomDto getRoomByName(String name);
    List<ScreeningRoomDto> getRoomsByBranchId(int branchId);
    ScreeningRoomDto updateRoom(int id, ScreeningRoomDto roomDto);
    boolean isRoomNameExists(String name);
    List<ScreeningRoomDto> getAllRooms();
    ScreeningRoomDto getRoomById(int id);
    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
    boolean deleteRoom(int id);
    Page<ScreeningRoomDto> getRoomsPage(int branchId, int page, int size);
} 