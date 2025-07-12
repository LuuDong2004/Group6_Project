package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.dto.ScreeningRoomDto;

public interface ScreeningRoomService {
    List<ScreeningRoomDto> getAllRooms();
    ScreeningRoomDto getRoomById(int id);
    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
    ScreeningRoomDto getRoomByName(String name);
    List<ScreeningRoomDto> getRoomsByBranchId(int branchId);
    ScreeningRoomDto updateRoom(int id, ScreeningRoomDto roomDto);
    boolean isRoomNameExists(String name);
    boolean deleteRoom(int id);
    // Có thể bổ sung các hàm validate, tìm kiếm nâng cao nếu cần
} 