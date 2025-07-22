package group6.cinema_project.service.admin;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.ScreeningRoomDto;

public interface AdminScreeningRoomService {
    ScreeningRoomDto getRoomByName(String name);
    List<ScreeningRoomDto> getRoomsByBranchId(int branchId);
    ScreeningRoomDto updateRoom(int id, ScreeningRoomDto roomDto);
    boolean isRoomNameExists(String name);
    List<ScreeningRoomDto> getAllRooms();
    ScreeningRoomDto getRoomById(int id);
    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
    boolean deleteRoom(int id);
    Page<ScreeningRoomDto> getRoomsPage(int branchId, int page, int size, String name, String type, String status, Integer rows, Integer seatsPerRow);
} 