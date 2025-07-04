package group6.cinema_project.controller;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/movies")
    public List<MovieDetailDTO> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/movies/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/movies/{id}/detail")
    public ResponseEntity<?> getMovieDetail(@PathVariable Long id) {
        MovieDetailDTO dto = movieService.getMovieDetail(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/movies/featured")
    public List<MovieDetailDTO> getFeaturedMovies() {
        return movieService.getFeaturedMovies();
    }

    @GetMapping("/genres")
    public List<String> getAllGenres() {
        return movieService.getAllGenres();
    }

    @GetMapping("/movies/genre/{genre}")
    public List<MovieDetailDTO> getMoviesByGenre(@PathVariable String genre) {
        return movieService.getMoviesByGenre(genre);
    }

    // DTO tổng hợp thông tin phim (giữ lại để dùng cho service và response)
    public static class MovieDetailDTO {
        public Long id;
        public String name;
        public String image;
        public int duration;
        public String release_date;
        public double rating;
        public String genre;
        public String language;
        public String format;
        public String trailer;
        public String summary;
        public java.util.List<String> directors;
        public java.util.List<String> actors;
        public java.util.List<ReviewDTO> reviews;
        public java.util.List<PersonDTO> actorsData;
        public java.util.List<PersonDTO> directorsData;
        public static class ReviewDTO {
            public String user;
            public String comment;
            public int rating;
            public String date;
        }
        public static class PersonDTO {
            public Long id;
            public String name;
        }
    }
}