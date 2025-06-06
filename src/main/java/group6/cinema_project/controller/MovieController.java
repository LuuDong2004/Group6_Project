package group6.cinema_project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.service.MovieService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> movies = movieService.findAll();
        System.out.println(movies);
        return "index";
    }


   
}
