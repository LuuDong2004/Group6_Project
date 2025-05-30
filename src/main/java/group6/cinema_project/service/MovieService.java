package group6.cinema_project.service;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.cinema_project.repository.MovieRepository;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieReponsitory;
    @Autowired
    private ModelMapper modelMapper;

    public MovieService() {
    }

    @Autowired
    public MovieService(MovieRepository movieReponsitory) {

        this.movieReponsitory = movieReponsitory;
    }

    @Override
    public List<MovieDto> getAllMovie() {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .map(movie -> modelMapper.map(movie , MovieDto.class))
                .collect(Collectors.toList());
    }
    public List<MovieDto> getMoviesByGenre(String gener){
        List<Movie> movies = movieReponsitory.getMoviesByGenre("Hoạt Hình");

        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }
}
