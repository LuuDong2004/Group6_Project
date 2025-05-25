package group6.cinema_project.controller;


//import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {
    @Autowired
    MovieRepository movieRepository;

    @PostMapping("save")
    public ResponseEntity<?> saveMovie(@RequestBody Movie movie) {
        movieRepository.save(movie);
        return new ResponseEntity<>("Movie saved successfully", HttpStatus.OK);
    }

    @GetMapping("get-all")
    public ResponseEntity<?> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }


}
