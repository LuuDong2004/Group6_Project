
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
     * Lọc phim theo tên (không phân biệt hoa thường)
     *
     * @param searchTerm Từ khóa tìm kiếm trong tên phim
     * @return Danh sách phim có tên chứa từ khóa
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo mô tả (không phân biệt hoa thường)
     *
     * @param searchTerm Từ khóa tìm kiếm trong mô tả phim
     * @return Danh sách phim có mô tả chứa từ khóa
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo thể loại (không phân biệt hoa thường)
     *
     * @param searchTerm Từ khóa tìm kiếm trong thể loại phim
     * @return Danh sách phim có thể loại chứa từ khóa
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByGenreContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo xếp hạng (không phân biệt hoa thường)
     *
     * @param searchTerm Từ khóa tìm kiếm trong xếp hạng phim
     * @return Danh sách phim có xếp hạng chứa từ khóa
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.rating) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByRatingContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo ngôn ngữ (không phân biệt hoa thường)
     *
     * @param searchTerm Từ khóa tìm kiếm trong ngôn ngữ phim
     * @return Danh sách phim có ngôn ngữ chứa từ khóa
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.language) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByLanguageContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo năm phát hành
     *
     * @param year Năm phát hành cần tìm
     * @return Danh sách phim phát hành trong năm đó
     */
    @Query("SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year")
    List<Movie> findByReleaseYear(@Param("year") Integer year);

    /**
     * Lọc phim theo tên đạo diễn (thông qua bảng liên kết)
     *
     * @param searchTerm Từ khóa tìm kiếm trong tên đạo diễn
     * @return Danh sách phim có đạo diễn với tên chứa từ khóa
     */
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.directors d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByDirectorNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Lọc phim theo tên diễn viên (thông qua bảng liên kết)
     *
     * @param searchTerm Từ khóa tìm kiếm trong tên diễn viên
     * @return Danh sách phim có diễn viên với tên chứa từ khóa
     */
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.actors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> findByActorNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

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
