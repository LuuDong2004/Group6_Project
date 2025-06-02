package group6.cinema_project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotDto {
    private int id;
    private String startTime;
    private String description;

}
