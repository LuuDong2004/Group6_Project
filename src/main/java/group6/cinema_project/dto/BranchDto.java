package group6.cinema_project.dto;

import group6.cinema_project.entity.Branch;
import jakarta.validation.constraints.NotBlank;

public class BranchDto {
    private int id;
    @NotBlank(message = "Tên chi nhánh không được để trống.")
    private String name;
    private String description;
    @NotBlank(message = "Địa chỉ không được để trống.")
    private String address;
    private int cinemaChainId = 0;

    public BranchDto() {
        this.cinemaChainId = 0;
    }


    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public int getCinemaChainId() {
        return cinemaChainId;
    }
    public void setCinemaChainId(Integer cinemaChainId) {
        this.cinemaChainId = (cinemaChainId == null) ? 0 : cinemaChainId;
    }

    // Convert DTO to Entity
    public Branch toEntity() {
        Branch branch = new Branch();
        branch.setId(this.id);
        branch.setName(this.name);
        branch.setDescription(this.description);
        branch.setAddress(this.address);
        // Không set cinemaChain ở đây (cần xử lý riêng nếu cần)
        return branch;
    }

    // Convert Entity to DTO
    public static BranchDto fromEntity(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setDescription(branch.getDescription());
        dto.setAddress(branch.getAddress());
        dto.setCinemaChainId(branch.getCinemaChain() != null ? branch.getCinemaChain().getId() : 0);
        return dto;
    }
}
