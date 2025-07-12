package group6.cinema_project.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.repository.ScreeningRoomRepository;
import group6.cinema_project.service.ScreeningRoomService;

@Service
public class ScreeningRoomServiceImpl implements ScreeningRoomService {
    @Autowired
    private ScreeningRoomRepository screeningRoomRepository;
    @Autowired
    private ModelMapper modelMapper;

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
    public ScreeningRoomDto saveOrUpdate(ScreeningRoomDto roomDto) {
        ScreeningRoom room = modelMapper.map(roomDto, ScreeningRoom.class);
        ScreeningRoom saved = screeningRoomRepository.save(room);
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