package group6.cinema_project.controller;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.DirectorMovieRepository;
import group6.cinema_project.repository.ReviewRepository;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Review;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.entity.DirectorMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorMovieRepository actorMovieRepository;

    @Autowired
    private DirectorMovieRepository directorMovieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/movies/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    // DTO tổng hợp thông tin phim
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

    @GetMapping("/movies/{id}/detail")
    public ResponseEntity<?> getMovieDetail(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) return ResponseEntity.notFound().build();
        MovieDetailDTO dto = new MovieDetailDTO();
        dto.id = movie.getId();
        dto.name = movie.getName();
        dto.image = movie.getImage();
        dto.duration = movie.getDuration();
        dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
        try { dto.rating = Double.parseDouble(movie.getRating()); } catch(Exception e) { dto.rating = 0; }
        dto.genre = movie.getGenre();
        dto.language = movie.getLanguage();
        dto.format = "3D"; // hoặc lấy từ trường khác nếu có
        dto.trailer = movie.getTrailer();
        dto.summary = ""; // Không còn trường description trong entity Movie
        dto.directors = directorMovieRepository.findByMovie_Id(id)
            .stream().map(DirectorMovie::getDirector)
            .map(Director::getName).collect(Collectors.toList());
        dto.actors = actorMovieRepository.findByMovieId(id)
            .stream()
            .map(ActorMovie::getActor)
            .map(Actor::getName)
            .distinct()
            .collect(Collectors.toList());
        dto.directorsData = directorMovieRepository.findByMovie_Id(id)
            .stream().map(dm -> {
                MovieDetailDTO.PersonDTO p = new MovieDetailDTO.PersonDTO();
                p.id = dm.getDirector().getId();
                p.name = dm.getDirector().getName();
                return p;
            }).collect(Collectors.toList());
        dto.actorsData = actorMovieRepository.findByMovieId(id)
            .stream().map(am -> {
                MovieDetailDTO.PersonDTO p = new MovieDetailDTO.PersonDTO();
                p.id = am.getActor().getId();
                p.name = am.getActor().getName();
                return p;
            }).collect(Collectors.toList());
        dto.reviews = reviewRepository.findByMovieId(id).stream().map(r -> {
            MovieDetailDTO.ReviewDTO rv = new MovieDetailDTO.ReviewDTO();
            rv.user = r.getUserId() != null ? r.getUserId().toString() : null;
            rv.comment = r.getComment();
            rv.rating = r.getRating();
            rv.date = r.getDate() != null ? r.getDate().toString() : null;
            return rv;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }
}