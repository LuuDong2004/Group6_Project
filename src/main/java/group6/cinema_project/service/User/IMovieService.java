package group6.cinema_project.service.User;

import group6.cinema_project.dto.MovieDto;


import java.util.List;

public interface IMovieService {
    List<MovieDto> getAllMovie();
    List<MovieDto> getMoviesWithPagination(int page, int size);
    List<MovieDto> findMovieById(Integer movieId);
    List<MovieDto> filterMovies(String genre, Integer year, String sort, String search);
    List<MovieDto> getTopMovies7Days();
    List<String> getAllGenres();
}
