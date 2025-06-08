package group6.cinema_project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class BranchDto {
    private int id;
    private String name;
    private String description;
    private String address;
    private boolean selected;
}
