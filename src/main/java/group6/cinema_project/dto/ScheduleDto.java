package group6.cinema_project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@NoArgsConstructor
@Data
@Getter
@Setter
public class ScheduleDto {
    private int id;
    private MovieDto movie;
    private ScreeningRoomDto screeningRoom;
    private BranchDto branch;
    private Date screeningDate;
    private Time startTime;
    private Time endTime;

}

