package group6.cinema_project.service.Admin.impl;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.repository.Admin.AdminScreeningRoomRepository;
import group6.cinema_project.service.Admin.IAdminRoomService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRoomServiceImpl implements IAdminRoomService {
    private final AdminScreeningRoomRepository screeningRoomRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<ScreeningRoomDto> getScreeningRoomById(Integer id) {
        return screeningRoomRepository.findById(id)
                .map(this::convertToBasicDto);
    }

    @Override
    public ScreeningRoomDto saveOrUpdateScreeningRoom(ScreeningRoomDto screeningRoomDto) {
        ScreeningRoom screeningRoom = modelMapper.map(screeningRoomDto, ScreeningRoom.class);
        ScreeningRoom savedRoom = screeningRoomRepository.save(screeningRoom);
        return modelMapper.map(savedRoom, ScreeningRoomDto.class);
    }

    @Override
    public void deleteScreeningRoom(Integer id) {
        if (!screeningRoomRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Screening room not found with ID: " + id);
        }
        screeningRoomRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningRoomDto> getAllScreeningRooms() {
        return screeningRoomRepository.findAll().stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningRoom> getAllScreeningRoomEntities() {
        return screeningRoomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningRoomDto> getScreeningRoomsByBranch(Integer branchId) {
        return screeningRoomRepository.findByBranchId(branchId).stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningRoomDto> getFilteredScreeningRooms(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllScreeningRooms();
        }
        return screeningRoomRepository.findByNameContainingIgnoreCase(searchTerm.trim()).stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert ScreeningRoom entity to DTO without triggering relationship loading
     * This method manually maps only the basic fields to avoid ModelMapper cascade
     * issues
     */
    private ScreeningRoomDto convertToBasicDto(ScreeningRoom room) {
        ScreeningRoomDto dto = new ScreeningRoomDto();

        // Map only basic fields to avoid relationship loading
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        dto.setType(room.getType());
        dto.setStatus(room.getStatus());
        dto.setRows(room.getRows());
        dto.setSeatsPerRow(room.getSeatsPerRow());

        if (room.getBranch() != null) {
            BranchDto branchDto = new BranchDto();
            branchDto.setId(room.getBranch().getId());
            branchDto.setName(room.getBranch().getName());
            dto.setBranch(branchDto);
        }

        // Don't map any relationships or collections to avoid cascade loading
        // These fields will remain null/empty, which is fine for dropdown purposes
        return dto;
    }
}
