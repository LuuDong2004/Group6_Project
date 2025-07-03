package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

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

        // Fetch all movies with directors and actors eagerly loaded for display
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "ORDER BY m.name")
        List<Movie> findAllWithDirectorsAndActors();

        // Fetch movies with directors and actors for filtered search
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByNameContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByDescriptionContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByGenreContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.rating) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByRatingContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByLanguageContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE YEAR(m.releaseDate) = :year " +
                        "ORDER BY m.name")
        List<Movie> findByReleaseYearWithDirectorsAndActors(@Param("year") Integer year);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors d " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByDirectorNameContainingIgnoreCaseWithDirectorsAndActors(
                        @Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors a " +
                        "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "ORDER BY m.name")
        List<Movie> findByActorNameContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);
}