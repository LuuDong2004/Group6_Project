package group6.cinema_project.controller;


//import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {
    @Autowired
    MovieService movieService;

    @PostMapping("save")
    @Transactional
    public ResponseEntity<?> saveMovie(@RequestBody MovieDto movieDto) {
        movieService.saveMovie(movieDto.convertToModel());
        return new ResponseEntity<>("Movie saved successfully", HttpStatus.OK);
    }

    @GetMapping("get-all")
    public ResponseEntity<?> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
