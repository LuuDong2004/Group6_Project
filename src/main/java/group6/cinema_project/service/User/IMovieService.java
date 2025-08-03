package group6.cinema_project.service.User;

import group6.cinema_project.dto.CustomerMovieDto;

import java.util.List;

public interface IMovieService {
    List<CustomerMovieDto> getAllMovie();

    List<CustomerMovieDto> getMoviesWithPagination(int page, int size);

    List<CustomerMovieDto> findMovieById(Integer movieId);

    CustomerMovieDto getMovieById(Integer movieId);

    CustomerMovieDto getMovieDetail(Integer movieId);

    List<CustomerMovieDto> filterMovies(String genre, Integer year, String sort, String search);

    List<CustomerMovieDto> getTopMovies7Days();

    List<String> getAllGenres();

}
