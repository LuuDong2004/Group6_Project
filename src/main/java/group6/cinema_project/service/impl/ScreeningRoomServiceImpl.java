package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.ScreeningRoomRepository;
import group6.cinema_project.repository.SeatRepository;
import group6.cinema_project.service.ScreeningRoomService;

@Service
public class ScreeningRoomServiceImpl implements ScreeningRoomService {
    @Autowired
    private ScreeningRoomRepository screeningRoomRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeatRepository seatRepository;

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
        }
        ScreeningRoom room = modelMapper.map(roomDto, ScreeningRoom.class);
        ScreeningRoom saved = screeningRoomRepository.save(room);

        // Xóa ghế cũ nếu cập nhật phòng chiếu
        if (room.getId() != 0) {
            List<Seat> oldSeats = seatRepository.findSeatsByScreeningRoomId(saved.getId());
            seatRepository.deleteAll(oldSeats);
        }

        // Tạo ghế mới
        int rows = roomDto.getRows();
        int seatsPerRow = roomDto.getSeatsPerRow();
        int standardRows = rows / 4; // 25%
        int vipRows = rows / 2;      // 50%
        int coupleRows = rows - standardRows - vipRows; // còn lại
        List<Seat> seats = new java.util.ArrayList<>();
        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            String seatType;
            if (i < standardRows) {
                seatType = "Standard";
            } else if (i < standardRows + vipRows) {
                seatType = "VIP";
            } else {
                seatType = "Couple";
            }
            for (int j = 1; j <= seatsPerRow; j++) {
                Seat seat = new Seat();
                seat.setRow(i); // lưu số thứ tự hàng, nếu muốn lưu ký tự thì sửa thành String.valueOf(rowChar)
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
    public boolean deleteRoom(int id) {
        if (!screeningRoomRepository.existsById(id)) return false;
        screeningRoomRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean isRoomNameExists(String name) {
        return screeningRoomRepository.findAll().stream().anyMatch(r -> r.getName().equalsIgnoreCase(name));
    }
} 