package group6.cinema_project.repository.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> getMoviesByGenre(String genre);

    @Query("SELECT m FROM Movie m ORDER BY m.rating DESC")
    List<Movie> getMoviesByTop3Rating(Pageable pageable);

    List<Movie> findMovieById(int id);

    @Query(value = "SELECT TOP (?1) m.*, COUNT(b.id) AS booking_count " +
            "FROM Movie m " +
            "LEFT JOIN ScreeningSchedule s ON s.movie_id = m.id " +
            "LEFT JOIN Booking b ON b.screening_schedule_id = s.id AND b.booking_date >= DATEADD(day, -7, GETDATE()) AND b.booking_date <= GETDATE() "
            +
            "GROUP BY m.id, m.description, m.duration, m.genre, m.image, m.language, m.name, m.rating, m.release_date, m.trailer, m.status "
            +
            "ORDER BY COUNT(b.id) DESC", nativeQuery = true)
    List<Movie> findTopMovies7Days(int topN);

    @Query("SELECT DISTINCT m.genre FROM Movie m")
    List<String> findAllGenres();

    List<Movie> findTop8ByOrderByRatingDesc();

    List<Movie> findTop8ByOrderByReleaseDateDesc();
}
