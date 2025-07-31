package group6.cinema_project.service.Admin.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.Admin.AdminScreeningRoomRepository;
import group6.cinema_project.repository.Admin.AdminSeatRepository;
import group6.cinema_project.service.Admin.IAdminScreeningRoomService;
import jakarta.transaction.Transactional;


@Service
public class AdminScreeningRoomServiceImpl implements IAdminScreeningRoomService {
    @Autowired
    private AdminScreeningRoomRepository screeningRoomRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AdminSeatRepository seatRepository;


    @Override
    public List<ScreeningRoomDto> getAllRooms() {
        return screeningRoomRepository.findAll().stream()
                .map(room -> modelMapper.map(room, ScreeningRoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ScreeningRoomDto getRoomById(int id) {
        Optional<ScreeningRoom> roomOpt = screeningRoomRepository.findById(id);
        return roomOpt.map(room -> modelMapper.map(room, ScreeningRoomDto.class)).orElse(null);
    }

    @Override
    @Transactional
    public ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto) {
        // Kiểm tra trùng tên phòng chiếu (không phân biệt hoa thường) bằng repository
        int branchId = roomDto.getBranch().getId();
        if (roomDto.getId() == 0) { // Thêm mới
            if (screeningRoomRepository.existsByBranchIdAndNameIgnoreCase(branchId, roomDto.getName())) {
                throw new RuntimeException("Tên phòng chiếu đã tồn tại trong chi nhánh này!");
            }
        } else { // Cập nhật
            if (screeningRoomRepository.existsByBranchIdAndNameIgnoreCaseAndIdNot(branchId, roomDto.getName(), roomDto.getId())) {
                throw new RuntimeException("Tên phòng chiếu đã tồn tại trong chi nhánh này!");
            }
            
            // Kiểm tra xem phòng có suất chiếu đang hoạt động không
            if (hasActiveSchedules(roomDto.getId())) {
                throw new RuntimeException("Không thể chỉnh sửa phòng chiếu vì có suất chiếu đang hoạt động!");
            }
        }
        ScreeningRoom room = modelMapper.map(roomDto, ScreeningRoom.class);
        ScreeningRoom saved = screeningRoomRepository.save(room);

        // Xóa ghế cũ nếu cập nhật phòng chiếu (chỉ xóa ghế chưa được đặt)
        if (room.getId() != 0) {
            List<Seat> oldSeats = seatRepository.findSeatsByScreeningRoomId(saved.getId());
            // Chỉ xóa những ghế chưa được đặt (không có SeatReservation)
            List<Seat> availableSeats = oldSeats.stream()
                .filter(seat -> !seatRepository.hasReservations(seat.getId()))
                .collect(Collectors.toList());
            seatRepository.deleteAll(availableSeats);
        }

        // Tạo ghế mới
        int rows = roomDto.getRows();
        int seatsPerRow = roomDto.getSeatsPerRow();
        int standardRows = rows / 4; // 25% hàng ghế thường
        int vipRows = rows / 2;      // 50% hàng ghế VIP
        int coupleRows = rows - standardRows - vipRows; // còn lại là hàng ghế đôi
        List<Seat> seats = new java.util.ArrayList<>();
        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            String seatType;
            if (i < standardRows) {
                seatType = "Standard"; // Ghế thường
            } else if (i < standardRows + vipRows) {
                seatType = "VIP"; // Ghế VIP
            } else {
                seatType = "Couple"; // Ghế đôi
            }
            for (int j = 1; j <= seatsPerRow; j++) {
                Seat seat = new Seat();
                seat.setRow(String.valueOf(rowChar)); // Lưu ký tự hàng
                seat.setName(rowChar + String.valueOf(j));
                seat.setRoom(saved);
                // Nếu entity Seat có seatType thì set:
                try { seat.getClass().getDeclaredField("seatType"); seat.getClass().getMethod("setSeatType", String.class).invoke(seat, seatType); } catch (Exception ignore) {}
                // Nếu entity Seat có status thì set mặc định AVAILABLE
                try { seat.getClass().getDeclaredField("status"); seat.getClass().getMethod("setStatus", String.class).invoke(seat, "AVAILABLE"); } catch (Exception ignore) {}
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        return modelMapper.map(saved, ScreeningRoomDto.class);
    }

    @Override
    public ScreeningRoomDto getRoomByName(String name) {
        Optional<ScreeningRoom> roomOpt = screeningRoomRepository.findAll().stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
        return roomOpt.map(room -> modelMapper.map(room, ScreeningRoomDto.class)).orElse(null);
    }

    @Override
    public List<ScreeningRoomDto> getRoomsByBranchId(int branchId) {
        return screeningRoomRepository.findAll().stream()
                .filter(room -> room.getBranch() != null && room.getBranch().getId() == branchId)
                .map(room -> modelMapper.map(room, ScreeningRoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ScreeningRoomDto updateRoom(int id, ScreeningRoomDto roomDto) {
        Optional<ScreeningRoom> roomOpt = screeningRoomRepository.findById(id);
        if (roomOpt.isEmpty()) return null;
        ScreeningRoom room = roomOpt.get();
        room.setName(roomDto.getName());
        room.setCapacity(roomDto.getCapacity());
        room.setDescription(roomDto.getDescription());
        room.setType(roomDto.getType());
        room.setStatus(roomDto.getStatus());
        room.setRows(roomDto.getRows());
        room.setSeatsPerRow(roomDto.getSeatsPerRow());
        // branch update nếu cần
        ScreeningRoom saved = screeningRoomRepository.save(room);
        return modelMapper.map(saved, ScreeningRoomDto.class);
    }

    @Override
    @Transactional
    public boolean deleteRoom(int id) {
        if (!screeningRoomRepository.existsById(id)) return false;
        
        // Kiểm tra xem có ghế nào đang được đặt không
        List<Seat> seats = seatRepository.findSeatsByScreeningRoomId(id);
        boolean hasReservedSeats = seats.stream()
            .anyMatch(seat -> seatRepository.hasReservations(seat.getId()));
        
        if (hasReservedSeats) {
            throw new RuntimeException("Không thể xóa phòng chiếu vì có ghế đang được đặt!");
        }
        
        // Xóa tất cả ghế trước khi xóa phòng
        seatRepository.deleteAll(seats);
        screeningRoomRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean isRoomNameExists(String name) {
        return screeningRoomRepository.findAll().stream().anyMatch(r -> r.getName().equalsIgnoreCase(name));
    }

    @Override
    public Page<ScreeningRoomDto> getRoomsPage(int branchId, int page, int size, String name, String type, String status, Integer rows, Integer seatsPerRow) {
        Page<ScreeningRoom> roomPage = screeningRoomRepository.searchRooms(branchId,
                (name != null && !name.trim().isEmpty()) ? name.trim() : null,
                (type != null && !type.trim().isEmpty()) ? type.trim() : null,
                (status != null && !status.trim().isEmpty()) ? status.trim() : null,
                rows,
                seatsPerRow,
                PageRequest.of(page, size)
        );
        List<ScreeningRoomDto> roomDtos = roomPage.getContent().stream().map(room -> modelMapper.map(room, ScreeningRoomDto.class)).collect(Collectors.toList());
        return new PageImpl<>(roomDtos, roomPage.getPageable(), roomPage.getTotalElements());
    }
    
    // Kiểm tra xem phòng chiếu có suất chiếu đang hoạt động không
    private boolean hasActiveSchedules(int roomId) {
        Date currentDate = new Date();
        java.sql.Time currentTime = new java.sql.Time(System.currentTimeMillis());
        Integer result = screeningRoomRepository.hasActiveSchedules(roomId, currentDate, currentTime);
        return result != null && result == 1;
    }
    
    @Override
    // Lấy danh sách suất chiếu đang hoạt động của phòng
    public List<ScreeningSchedule> getActiveSchedules(int roomId) {
        Date currentDate = new Date();
        java.sql.Time currentTime = new java.sql.Time(System.currentTimeMillis());
        return screeningRoomRepository.getActiveSchedules(roomId, currentDate, currentTime);
    }
    
    @Override
    // Kiểm tra xem phòng chiếu có thể chỉnh sửa được không
    public boolean canEditRoom(int roomId) {
        return !hasActiveSchedules(roomId);
    }
}
