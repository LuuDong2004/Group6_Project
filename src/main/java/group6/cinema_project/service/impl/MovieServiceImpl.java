package group6.cinema_project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.service.MovieService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Movie saveOrUpdate(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void delete(Movie movie) {
        movieRepository.delete(movie);
    }
    
}
