package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminMovieRepository extends JpaRepository<Movie, Integer> {

        /**
         * Lấy tất cả phim kèm thông tin đạo diễn và diễn viên (eager loading) để hiển
         * thị - chỉ lấy phim chưa bị soft delete
         *
         * @return Danh sách phim với thông tin đầy đủ về đạo diễn và diễn viên (chưa bị
         *         xóa)
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findAllWithDirectorsAndActors();

        /**
         * Lấy phim theo ID kèm thông tin đạo diễn và diễn viên (eager loading) để chỉnh
         * sửa - chỉ lấy phim chưa bị soft delete
         *
         * @param id ID của phim cần lấy
         * @return Phim với thông tin đầy đủ về đạo diễn và diễn viên (chưa bị xóa)
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE m.id = :id AND m.isDeleted = false")
        Optional<Movie> findByIdWithDirectorsAndActors(@Param("id") Integer id);

        /**
         * Override findAll() để chỉ lấy phim chưa bị soft delete
         *
         * @return Danh sách phim chưa bị xóa
         */
        @Query("SELECT m FROM Movie m WHERE m.isDeleted = false ORDER BY m.name")
        List<Movie> findAll();

        /**
         * Lấy phim theo ID bao gồm cả phim đã bị soft delete (dùng cho restore)
         *
         * @param id ID của phim cần lấy
         * @return Phim với thông tin đầy đủ (bao gồm cả đã bị xóa)
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE m.id = :id")
        Optional<Movie> findByIdIncludingDeleted(@Param("id") Integer id);

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
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByNameContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByDescriptionContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByGenreContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.rating) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByRatingContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.language) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByLanguageContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE YEAR(m.releaseDate) = :year " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByReleaseYearWithDirectorsAndActors(@Param("year") Integer year);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors d " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByDirectorNameContainingIgnoreCaseWithDirectorsAndActors(
                        @Param("searchTerm") String searchTerm);

        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors a " +
                        "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        List<Movie> findByActorNameContainingIgnoreCaseWithDirectorsAndActors(@Param("searchTerm") String searchTerm);

        // Pagination methods
        /**
         * Lấy tất cả phim kèm thông tin đạo diễn và diễn viên với phân trang - chỉ phim
         * chưa bị xóa
         *
         * @param pageable Thông tin phân trang
         * @return Page chứa danh sách phim với thông tin đầy đủ (chưa bị xóa)
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE m.isDeleted = false " +
                        "ORDER BY m.name")
        Page<Movie> findAllWithDirectorsAndActorsPageable(Pageable pageable);

        /**
         * Lấy phim theo tên kèm thông tin đạo diễn và diễn viên với phân trang
         *
         * @param searchTerm Từ khóa tìm kiếm trong tên phim
         * @param pageable   Thông tin phân trang
         * @return Page chứa danh sách phim có tên chứa từ khóa với thông tin đầy đủ
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        Page<Movie> findByNameContainingIgnoreCaseWithDirectorsAndActorsPageable(@Param("searchTerm") String searchTerm,
                        Pageable pageable);

        /**
         * Lấy phim theo mô tả kèm thông tin đạo diễn và diễn viên với phân trang
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        Page<Movie> findByDescriptionContainingIgnoreCaseWithDirectorsAndActorsPageable(
                        @Param("searchTerm") String searchTerm, Pageable pageable);

        /**
         * Lấy phim theo thể loại kèm thông tin đạo diễn và diễn viên với phân trang
         */ 
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        Page<Movie> findByGenreContainingIgnoreCaseWithDirectorsAndActorsPageable(
                        @Param("searchTerm") String searchTerm, Pageable pageable);

        /**
         * Lấy phim theo rating kèm thông tin đạo diễn và diễn viên với phân trang
         */
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN FETCH m.directors " +
                        "LEFT JOIN FETCH m.actors " +
                        "WHERE LOWER(m.rating) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "AND m.isDeleted = false " +
                        "ORDER BY m.name")
        Page<Movie> findByRatingContainingIgnoreCaseWithDirectorsAndActorsPageable(
                        @Param("searchTerm") String searchTerm, Pageable pageable);
}
