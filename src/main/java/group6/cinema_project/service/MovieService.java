package group6.cinema_project.service;

import group6.cinema_project.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.cinema_project.repository.MovieRepository;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieReponsitory;
    public MovieService() {
    }
    @Autowired
    public MovieService(MovieRepository movieReponsitory) {
        this.movieReponsitory = movieReponsitory;
    }
    public List<Movie> getAllMovies() {
        return movieReponsitory.findAll();
    }
    public Movie saveMovie(Movie movie) {
        return movieReponsitory.save(movie);
    }
    public void deleteMoive(int id){
        movieReponsitory.deleteById(id);
    }

}
