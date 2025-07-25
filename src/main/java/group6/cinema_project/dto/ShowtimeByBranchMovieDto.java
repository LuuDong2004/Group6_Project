package group6.cinema_project.dto;

import lombok.Data;
import java.util.List;

@Data
public class ShowtimeByBranchMovieDto {
    private Integer id;
    private String title;
    private String poster;
    private List<ScheduleDto> schedules;

    @Data
    public static class ScheduleDto {
        private Integer id;
        private String screeningDate; // yyyy-MM-dd
        private String startTime;     // HH:mm
        private Integer roomId;
    }
} 