package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface AdminMovieRepository extends JpaRepository<Movie, Integer> {

        /**
         * Lấy tất cả phim kèm thông tin đạo diễn và diễn viên (eager loading) để hiển
         * thị
         * 
         * @return Danh sách phim với thông tin đầy đủ về đạo diễn và diễn viên
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "ORDER BY m.name")
        List<Movie> findAllWithDirectorsAndActors();

        /**
         * Lấy phim theo tên kèm thông tin đạo diễn và diễn viên cho tìm kiếm có lọc
         * 
         * @param searchTerm Từ khóa tìm kiếm trong tên phim
         * @return Danh sách phim có tên chứa từ khóa với thông tin đầy đủ
         */
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
