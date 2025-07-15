package group6.cinema_project.service.impl;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.ReviewRepository;
import group6.cinema_project.dto.MovieDetailDto;
import group6.cinema_project.dto.PersonDto;
import group6.cinema_project.dto.ReviewDto;
import group6.cinema_project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ActorMovieRepository actorMovieRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<MovieDetailDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(movie -> {
            MovieDetailDto dto = new MovieDetailDto();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public MovieDetailDto getMovieDetail(Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) return null;
        MovieDetailDto dto = new MovieDetailDto();
        dto.id = movie.getId();
        dto.name = movie.getName();
        dto.image = movie.getImage();
        dto.duration = movie.getDuration();
        dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
        try { dto.rating = Double.parseDouble(movie.getRating()); } catch(Exception e) { dto.rating = 0; }
        dto.genre = movie.getGenre();
        dto.language = movie.getLanguage();
        dto.format = "3D";
        dto.trailer = movie.getTrailer();
        dto.summary = "";
        dto.directors = movie.getDirectors() != null ? movie.getDirectors().stream().map(Director::getName).collect(Collectors.toList()) : List.of();
        dto.actors = actorMovieRepository.findByMovieId(id)
            .stream()
            .map(ActorMovie::getActor)
            .map(Actor::getName)
            .distinct()
            .collect(Collectors.toList());
        dto.directorsData = movie.getDirectors() != null ? movie.getDirectors().stream().map(d -> { PersonDto p = new PersonDto(); p.id = d.getId(); p.name = d.getName(); return p; }).collect(Collectors.toList()) : List.of();
        dto.actorsData = actorMovieRepository.findByMovieId(id)
            .stream().map(am -> {
                PersonDto p = new PersonDto();
                p.id = am.getActor().getId();
                p.name = am.getActor().getName();
                return p;
            }).collect(Collectors.toList());
        dto.reviews = reviewRepository.findByMovieId(id).stream().map(r -> {
            ReviewDto rv = new ReviewDto();
            rv.user = r.getUserId() != null ? r.getUserId().toString() : null;
            rv.comment = r.getComment();
            rv.rating = r.getRating();
            rv.date = r.getDate() != null ? r.getDate().toString() : null;
            return rv;
        }).collect(Collectors.toList());
        return dto;
    }

    @Override
    public List<MovieDetailDto> getFeaturedMovies() {
        List<Movie> movies = movieRepository.findAll();
        movies.sort(Comparator.comparing(Movie::getId));
        List<MovieDetailDto> dtos = movies.stream().map(movie -> {
            MovieDetailDto dto = new MovieDetailDto();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        })
        .filter(dto -> dto.rating >= 0 && dto.rating <= 10)
        .sorted((a, b) -> Double.compare(b.rating, a.rating))
        .collect(Collectors.toList());
        return dtos.size() > 6 ? dtos.subList(0, 6) : dtos;
    }

    @Override
    public List<String> getAllGenres() {
        return movieRepository.findAllGenres();
    }

    @Override
    public List<MovieDetailDto> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies.stream().map(movie -> {
            MovieDetailDto dto = new MovieDetailDto();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        }).collect(Collectors.toList());
    }
} 