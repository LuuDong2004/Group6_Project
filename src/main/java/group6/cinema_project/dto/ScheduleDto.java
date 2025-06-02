package group6.cinema_project.dto;

import group6.cinema_project.entity.Branch;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.ScreeningTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
@Getter
@Setter
public class ScheduleDto {

    private int id;
    //    private LocalDate startDate;
//    private LocalTime startTime;
    private MovieDto movie;
    private ScreeningTimeDto screeningTimeSlot;
    private ScreeningRoomDto screeningRoom;
    private BranchDto branch;
}

