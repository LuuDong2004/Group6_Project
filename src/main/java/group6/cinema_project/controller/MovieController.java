package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("movie")
public class MovieController {
    @Autowired
    IMovieService movieService;

    @GetMapping("view")
    public String getAllMovies(Model model) {
        List<MovieDto> movies = movieService.getAllMovie();
        model.addAttribute("movies", movies);

        List<MovieDto> moviesKid = movieService.getMoviesByGenre("Hoạt Hình");
        model.addAttribute("moviesKid", moviesKid);

        return "movies";
    }

}
