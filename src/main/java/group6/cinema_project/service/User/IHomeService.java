package group6.cinema_project.service.User;

import group6.cinema_project.dto.MovieDto;
import java.util.List;

public interface IHomeService {
    List<MovieDto> getPopularMovies();
    List<MovieDto> getNewReleases();
} 