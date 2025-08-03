package group6.cinema_project.service.Admin;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningSchedule;

public interface IAdminScreeningRoomService {
//    List<ScreeningRoomDto> getAllRooms();
//    ScreeningRoomDto getRoomById(int id);
//    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
//    boolean deleteRoom(int id);
//    // Additional methods for controller/service usage
//    Page<ScreeningRoomDto> getRoomsPage(int branchId, int page, int size);
//    List<ScreeningRoomDto> getRoomsByBranchId(int branchId);
//    group6.cinema_project.dto.ScreeningRoomDto getRoomByName(String name);
//    group6.cinema_project.dto.ScreeningRoomDto updateRoom(int id, group6.cinema_project.dto.ScreeningRoomDto roomDto);
//    boolean isRoomNameExists(String name);

    ScreeningRoomDto getRoomByName(String name);
    List<ScreeningRoomDto> getRoomsByBranchId(int branchId);
    ScreeningRoomDto updateRoom(int id, ScreeningRoomDto roomDto);
    boolean isRoomNameExists(String name);
    List<ScreeningRoomDto> getAllRooms();
    ScreeningRoomDto getRoomById(int id);
    ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto);
    boolean deleteRoom(int id);
    Page<ScreeningRoomDto> getRoomsPage(int branchId, int page, int size, String name, String type, String status, Integer rows, Integer seatsPerRow);
    
    // Kiểm tra xem phòng chiếu có thể chỉnh sửa được không
    boolean canEditRoom(int roomId);
    
    // Lấy danh sách suất chiếu đang hoạt động của phòng
    List<ScreeningSchedule> getActiveSchedules(int roomId);
}

