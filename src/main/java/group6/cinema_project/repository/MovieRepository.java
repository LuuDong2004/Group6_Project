package group6.cinema_project.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> getMoviesByGenre(String genre);

    @Query("SELECT m FROM Movie m ORDER BY m.rating DESC")
    List<Movie> getMoviesByTop3Rating(Pageable pageable);

    // Filter by movie name (title)
    @Query("SELECT m FROM Movie m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by description
    @Query("SELECT m FROM Movie m WHERE LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by genre
    @Query("SELECT m FROM Movie m WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByGenreContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by rating
    @Query("SELECT m FROM Movie m WHERE LOWER(m.rating) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByRatingContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by language
    @Query("SELECT m FROM Movie m WHERE LOWER(m.language) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByLanguageContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by release year
    @Query("SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year")
    List<Movie> findByReleaseYear(@Param("year") Integer year);

    // Filter by director name (through junction table)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.directors d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByDirectorNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Filter by actor name (through junction table)
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.actors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByActorNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
}