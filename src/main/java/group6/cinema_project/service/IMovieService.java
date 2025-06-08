package group6.cinema_project.service;

import group6.cinema_project.dto.MovieDto;


import java.util.List;

public interface IMovieService {
    List<MovieDto> getMoviesByGenre(String genre);
    List<MovieDto> getAllMovie();
    List<MovieDto> getMoviesByTop3Rating();
    List<MovieDto> getMoviesWithPagination(int page, int size);
    List<MovieDto> findMovieById(Integer movieId);
}