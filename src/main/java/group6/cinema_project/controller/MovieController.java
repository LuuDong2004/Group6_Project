package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        List<MovieDto> allMovies = movieService.getMoviesWithPagination(0,8);
        model.addAttribute("Movies", allMovies);

        // Lấy danh sách phim theo thể loại
        List<MovieDto> filteredMovies = movieService.getMoviesByGenre(genre);
        model.addAttribute("filteredMovies", filteredMovies);
        model.addAttribute("selected", genre);

        return "movies";
    }

    @GetMapping("/loadMore")
    @ResponseBody
    public ResponseEntity<List<MovieDto>> loadMoreMovies(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "8") int size) {
        List<MovieDto> movies = movieService.getMoviesWithPagination(page, size);
        return ResponseEntity.ok(movies);
    }

    // API endpoint để load tất cả phim
    @GetMapping("/loadAll")
    @ResponseBody
    public ResponseEntity<List<MovieDto>> loadAllMovies() {
        List<MovieDto> allMovies = movieService.getAllMovie();
        return ResponseEntity.ok(allMovies);
    }
}
