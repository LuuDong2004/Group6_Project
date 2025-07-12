package group6.cinema_project.dto;

import group6.cinema_project.entity.Region;

public class RegionDto {
    private int id;
    private String type;
    private String name;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Convert DTO to Entity
    public Region toEntity() {
        Region region = new Region();
        region.setId(this.id);
        region.setType(this.type);
        region.setName(this.name);
        return region;
    }

    // Convert Entity to DTO
    public static RegionDto fromEntity(Region region) {
        RegionDto dto = new RegionDto();
        dto.setId(region.getId());
        dto.setType(region.getType());
        dto.setName(region.getName());
        return dto;
    }
} 