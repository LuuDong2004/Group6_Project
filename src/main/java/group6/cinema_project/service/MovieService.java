package group6.cinema_project.service;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.dto.MovieDetailDto;
import java.util.List;

public interface MovieService {
    List<MovieDetailDto> getAllMovies();
    Movie getMovieById(Long id);
    MovieDetailDto getMovieDetail(Long id);
    List<MovieDetailDto> getFeaturedMovies();
    List<String> getAllGenres();
    List<MovieDetailDto> getMoviesByGenre(String genre);
} 