package group6.cinema_project.service;

import group6.cinema_project.dto.ScheduleDto;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IScheduleService {

    List<ScheduleDto> getScheduleByMovieId(Integer movieId);

}
