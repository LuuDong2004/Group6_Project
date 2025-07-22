package group6.cinema_project.dto;

import group6.cinema_project.entity.ScreeningRoom;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ScreeningRoomDto {

    private int id;
    @NotBlank(message = "Tên phòng không được để trống.")
    private String name;
    @NotNull(message = "Sức chứa không được để trống.")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0.")
    private Integer capacity; // sức chứa
    private String description;
    private String type;
    private String status;
    private BranchDto branch;
    
    // Thêm các field mới
    private int rows; // số hàng ghế
    private int seatsPerRow; // số ghế mỗi hàng

    public static ScreeningRoomDto fromEntity(ScreeningRoom room) {
        if (room == null) return null;
        ScreeningRoomDto dto = new ScreeningRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        dto.setType(room.getType());
        dto.setStatus(room.getStatus());
        if (room.getBranch() != null) {
            dto.setBranch(BranchDto.fromEntity(room.getBranch()));
        }
        dto.setRows(room.getRows());
        dto.setSeatsPerRow(room.getSeatsPerRow());
        return dto;
    }

    public ScreeningRoom toEntity() {
        ScreeningRoom room = new ScreeningRoom();
        room.setId(this.id);
        room.setName(this.name);
        room.setCapacity(this.capacity);
        room.setDescription(this.description);
        room.setType(this.type);
        room.setStatus(this.status);
        // Không set branch ở đây (cần xử lý riêng nếu cần)
        room.setRows(this.rows);
        room.setSeatsPerRow(this.seatsPerRow);
        return room;
    }
}