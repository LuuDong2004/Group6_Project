package group6.cinema_project.dto;

import group6.cinema_project.entity.CinemaChain;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CinemaChainDto {
    private int id;
    @NotBlank(message = "Tên chuỗi rạp không được để trống.")
    private String name;
    private String description;

    // Convert Entity to DTO
    public static CinemaChainDto fromEntity(CinemaChain entity) {
        CinemaChainDto dto = new CinemaChainDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    // Convert DTO to Entity
    public CinemaChain toEntity() {
        CinemaChain entity = new CinemaChain();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        return entity;
    }
} 