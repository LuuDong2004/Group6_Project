package group6.cinema_project.controller;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.service.MovieService;
import group6.cinema_project.dto.MovieDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/")
    public String home(Model model) {
        List<MovieDetailDTO> featuredMovies = movieService.getFeaturedMovies();
        model.addAttribute("featuredMovies", featuredMovies);
        return "index";
    }

    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        List<MovieDetailDTO> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        return "movies";
    }

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable("id") Long id, Model model) {
        MovieDetailDTO dto = movieService.getMovieDetail(id);
        if (dto == null) {
            return "redirect:/movies";
        }
        model.addAttribute("movie", dto);
        return "movie_detail";
    }

    @GetMapping("/movies/featured")
    public String getFeaturedMovies(Model model) {
        List<MovieDetailDTO> featuredMovies = movieService.getFeaturedMovies();
        model.addAttribute("featuredMovies", featuredMovies);
        return "movies";
    }

    @GetMapping("/genres")
    public String getAllGenres(Model model) {
        List<String> genres = movieService.getAllGenres();
        model.addAttribute("genres", genres);
        return "genres";
    }

    @GetMapping("/movies/genre/{genre}")
    public String getMoviesByGenre(@PathVariable String genre, Model model) {
        List<MovieDetailDTO> movies = movieService.getMoviesByGenre(genre);
        model.addAttribute("movies", movies);
        model.addAttribute("selectedGenre", genre);
        return "movies";
    }
}