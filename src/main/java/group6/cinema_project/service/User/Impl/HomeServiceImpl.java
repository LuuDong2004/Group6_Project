package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IHomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<MovieDto> getPopularMovies() {
        List<Movie> movies = movieRepository.findTop8ByOrderByRatingDesc();
        return movies.stream().map(m -> modelMapper.map(m, MovieDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> getNewReleases() {
        List<Movie> movies = movieRepository.findTop8ByOrderByReleaseDateDesc();
        return movies.stream().map(m -> modelMapper.map(m, MovieDto.class)).collect(Collectors.toList());
    }
} 