package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleDto;
import group6.cinema_project.service.IMovieService;
import group6.cinema_project.service.IScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("schedule")
public class ScheduleController {
    @Autowired
    private IScheduleService scheduleService;
    @Autowired
    private IMovieService movieService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/movie/{movieId}")
    public String getSchedulesByMovieId(@PathVariable(name = "movieId") Integer movieId, Model model) {
        List<ScheduleDto> schedules = scheduleService.getScheduleByMovieId(movieId);
        List<MovieDto> movies = movieService.findMovieById(movieId);
        model.addAttribute("movie",movies);
        model.addAttribute("schedules", schedules);
        return "ticket-booking";
    }
}
