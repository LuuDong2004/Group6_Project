package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("movie")
public class MovieController {
    @Autowired
    IMovieService movieService;

    @GetMapping("view")
    public String getAllMoviesAndByGenre(@RequestParam(value = "genre", required = false) String genre, Model model) {

        List<MovieDto> moviesByRate = movieService.getMoviesByTop3Rating();
        model.addAttribute("topMovies", moviesByRate);
        moviesByRate.forEach(movie -> {
            System.out.println("Movie: " + movie.getName());
        });
        // Lấy tất cả các phim
        List<MovieDto> allMovies = movieService.getAllMovie();
        model.addAttribute("Movies", allMovies);

        // Lấy danh sách phim theo thể loại
        List<MovieDto> filteredMovies = movieService.getMoviesByGenre(genre);
        model.addAttribute("filteredMovies", filteredMovies);
        model.addAttribute("selected", genre);

        return "movies";
    }




}
