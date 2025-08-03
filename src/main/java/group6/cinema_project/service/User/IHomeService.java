package group6.cinema_project.service.User;

import group6.cinema_project.dto.CustomerMovieDto;
import java.util.List;

public interface IHomeService {
    List<CustomerMovieDto> getPopularMovies();

    List<CustomerMovieDto> getNewReleases();
}