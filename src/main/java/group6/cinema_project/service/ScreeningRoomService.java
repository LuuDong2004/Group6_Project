package group6.cinema_project.service;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningRoom;

import java.util.List;
import java.util.Optional;

public interface ScreeningRoomService {
    
    Optional<ScreeningRoomDto> getScreeningRoomById(Integer id);
    
    ScreeningRoomDto saveOrUpdateScreeningRoom(ScreeningRoomDto screeningRoomDto);
    
    void deleteScreeningRoom(Integer id);
    
    List<ScreeningRoomDto> getAllScreeningRooms();
    
    List<ScreeningRoom> getAllScreeningRoomEntities();
    
    List<ScreeningRoomDto> getScreeningRoomsByBranch(Integer branchId);
    
    List<ScreeningRoomDto> getFilteredScreeningRooms(String searchTerm);
    
}
