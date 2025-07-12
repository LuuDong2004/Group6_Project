package group6.cinema_project.dto;

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

    private String name;
    private int capacity; // sức chứa
    private String description;
    private String type;
    private String status;
    private BranchDto branch;
}