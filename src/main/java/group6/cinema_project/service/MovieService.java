package group6.cinema_project.service;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.controller.MovieController.MovieDetailDTO;
import java.util.List;

public interface MovieService {
    List<MovieDetailDTO> getAllMovies();
    Movie getMovieById(Long id);
    MovieDetailDTO getMovieDetail(Long id);
    List<MovieDetailDTO> getFeaturedMovies();
    List<String> getAllGenres();
    List<MovieDetailDTO> getMoviesByGenre(String genre);
} 