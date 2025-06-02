package group6.cinema_project.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> getMoviesByGenre(String genre);
    @Query("SELECT m FROM Movie m ORDER BY m.rating DESC")
    List<Movie> getMoviesByTop3Rating(Pageable pageable);

    List<Movie> findMovieById(int id);

}
