package group6.cinema_project.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BranchDto {
    private int id;
    private String name;
    private String description;
    private String address;
    private int cinemaChainId;
    private boolean selected;
}
