package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.MovieDto;
import java.util.List;
import java.util.Optional;

public interface IAdminMovieService {
    Optional<MovieDto> getMovieById(Integer id);

    MovieDto saveOrUpdate(MovieDto movieDto);

    void deleteMovie(Integer id);

    List<MovieDto> getAllMovie();

    // New methods for display with directors and actors
    List<MovieDto> getAllMoviesForDisplay();

    List<MovieDto> getFilteredMoviesForDisplay(String searchTerm, String filterBy);
}
