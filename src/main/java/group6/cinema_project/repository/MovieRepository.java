package group6.cinema_project.repository;
import group6.cinema_project.dto.MovieDto;
import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> getMoviesByGenre(String genre);

}
