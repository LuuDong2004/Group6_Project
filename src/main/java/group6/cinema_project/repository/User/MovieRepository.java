package group6.cinema_project.repository.User;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import group6.cinema_project.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.name = :genre")
    List<Movie> getMoviesByGenre(@Param("genre") String genre);

    @Query("SELECT m FROM Movie m LEFT JOIN m.rating r ORDER BY r.code DESC")
    List<Movie> getMoviesByTop3Rating(Pageable pageable);

    List<Movie> findMovieById(int id);

    @Query(value = "SELECT TOP (?1) m.id, m.name, m.image, m.duration, m.release_date, m.rating_id, m.language, m.trailer, m.description, m.status, COUNT(b.id) AS booking_count "
            +
            "FROM Movie m " +
            "LEFT JOIN ScreeningSchedule s ON s.movie_id = m.id " +
            "LEFT JOIN Booking b ON b.screening_schedule_id = s.id AND b.booking_date >= DATEADD(day, -7, GETDATE()) AND b.booking_date <= GETDATE() "
            +
            "GROUP BY m.id, m.name, m.image, m.duration, m.release_date, m.rating_id, m.language, m.trailer, m.description, m.status "
            +
            "ORDER BY COUNT(b.id) DESC", nativeQuery = true)
    List<Movie> findTopMovies7Days(int topN);

    @Query("SELECT DISTINCT g.name FROM Movie m JOIN m.genres g")
    List<String> findAllGenres();

    @Query("SELECT m FROM Movie m LEFT JOIN m.rating r ORDER BY r.code DESC")
    List<Movie> findTop8ByOrderByRatingDesc(Pageable pageable);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.actors LEFT JOIN FETCH m.directors ORDER BY m.rating DESC")
    List<Movie> findTop8ByOrderByRatingDesc();

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.actors LEFT JOIN FETCH m.directors ORDER BY m.releaseDate DESC")
    List<Movie> findTop8ByOrderByReleaseDateDesc();

    /**
     * Lấy một phim theo ID kèm thông tin genres, directors và actors (eager
     * loading)
     * Sử dụng cho movie detail page
     */
    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN FETCH m.genres " +
            "LEFT JOIN FETCH m.rating " +
            "LEFT JOIN FETCH m.directors " +
            "LEFT JOIN FETCH m.actors " +
            "WHERE m.id = :id")
    Optional<Movie> findByIdWithAllRelations(@Param("id") Integer id);

}
