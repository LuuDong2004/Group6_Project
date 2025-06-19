package group6.cinema_project.repository;

import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m LEFT JOIN m.reviews r GROUP BY m.id ORDER BY AVG(r.rating) DESC")
    List<Movie> findTopMoviesByAverageRating();

    @Query("SELECT DISTINCT m.genre FROM Movie m")
    List<String> findAllGenres();

    List<Movie> findByGenre(String genre);
}