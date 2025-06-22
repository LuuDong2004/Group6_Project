package group6.cinema_project.service;

import group6.cinema_project.dto.MovieDto;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<MovieDto> getMovieById(Integer id);

    MovieDto saveOrUpdate(MovieDto movieDto);

    void deleteMovie(Integer id);

    List<MovieDto> getAllMovie();

    List<MovieDto> getFilteredMovies(String searchTerm, String filterBy);

}