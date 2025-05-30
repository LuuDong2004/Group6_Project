package group6.cinema_project.service;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;

import java.util.List;

public interface IMovieService {
    List<MovieDto> getMoviesByGenre(String genre);
    List<MovieDto> getAllMovie();
}
