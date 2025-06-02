package group6.cinema_project.dto;

import group6.cinema_project.entity.Date;
import group6.cinema_project.entity.Slot;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScreeningTimeDto {
    private int id;
    private String description;
    private SlotDto slot; // slotId
    private DateDto date;
}
