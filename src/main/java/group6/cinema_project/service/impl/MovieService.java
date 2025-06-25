package group6.cinema_project.service.impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Movie;

import group6.cinema_project.service.IMovieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;
import group6.cinema_project.repository.MovieRepository;




import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieReponsitory;
    @Autowired
    private ModelMapper modelMapper;

//    public MovieService() {
//    }
//
//    @Autowired
//    public MovieService(MovieRepository movieReponsitory) {
//
//        this.movieReponsitory = movieReponsitory;
//    }

    @Override
    public List<MovieDto> getAllMovie() {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .map(movie -> modelMapper.map(movie , MovieDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<MovieDto> getMoviesByGenre(String gener){
        List<Movie> movies = movieReponsitory.getMoviesByGenre(gener);

        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<MovieDto> getMoviesByTop3Rating(){
        Pageable top3 = PageRequest.of(0, 3);
        List<Movie> movies = movieReponsitory.getMoviesByTop3Rating(top3);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<MovieDto> getMoviesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Movie> movies = movieReponsitory.findAll(pageable).getContent();
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    public List<MovieDto> findMovieById(Integer moiveId){
        List<Movie> movies = movieReponsitory.findMovieById(moiveId);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

}
