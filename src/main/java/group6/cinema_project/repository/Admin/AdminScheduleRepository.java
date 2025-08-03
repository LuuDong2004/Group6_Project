
package group6.cinema_project.repository.Admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningSchedule;

@Repository
public interface AdminScheduleRepository extends JpaRepository<ScreeningSchedule, Integer> {

    /**
     * Lấy danh sách lịch chiếu theo ID phim
     *
     * @param movieId ID của phim
     * @return Danh sách lịch chiếu của phim
     */
    List<ScreeningSchedule> getScreeningSchedulesByMovieId(Integer movieId);

    /**
     * Lấy tất cả lịch chiếu kèm thông tin liên quan (phim, phòng chiếu, chi nhánh)
     * Sắp xếp theo tên phim, ngày chiếu, giờ bắt đầu
     *
     * @return Danh sách lịch chiếu với thông tin đầy đủ
     */
    @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
            "LEFT JOIN FETCH ss.movie m " +
            "LEFT JOIN FETCH ss.screeningRoom sr " +
            "LEFT JOIN FETCH ss.branch b " +
            "ORDER BY m.name, ss.screeningDate, ss.startTime")
    List<ScreeningSchedule> findAllWithRelatedEntities();

    /**
     * Lấy lịch chiếu có lọc theo điều kiện kèm thông tin liên quan
     *
     * @param movieId         ID phim (có thể null)
     * @param screeningDate   Ngày chiếu (có thể null)
     * @param screeningRoomId ID phòng chiếu (có thể null)
     * @return Danh sách lịch chiếu đã lọc với thông tin đầy đủ
     */
    @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
            "LEFT JOIN FETCH ss.movie m " +
            "LEFT JOIN FETCH ss.screeningRoom sr " +
            "LEFT JOIN FETCH ss.branch b " +
            "WHERE (:movieId IS NULL OR ss.movie.id = :movieId) " +
            "AND (:screeningDate IS NULL OR ss.screeningDate = :screeningDate) " +
            "AND (:screeningRoomId IS NULL OR ss.screeningRoom.id = :screeningRoomId) " +
            "ORDER BY m.name, ss.screeningDate, ss.startTime")
    List<ScreeningSchedule> findFilteredWithRelatedEntities(
            @Param("movieId") Integer movieId,
            @Param("screeningDate") LocalDate screeningDate,
            @Param("screeningRoomId") Integer screeningRoomId);

    /**
     * Tìm phim theo trạng thái lịch chiếu (sử dụng JPQL)
     *
     * @param status Trạng thái lịch chiếu
     * @return Danh sách phim có trạng thái tương ứng
     */
    @Query("SELECT DISTINCT m FROM ScreeningSchedule ss " +
            "JOIN ss.movie m " +
            "WHERE ss.status = :status " +
            "ORDER BY m.name")
    List<group6.cinema_project.entity.Movie> findMoviesByScheduleStatus(@Param("status") String status);

    /**
     * Tìm phim theo trạng thái lịch chiếu (sử dụng Native SQL - phương án dự phòng)
     *
     * @param status Trạng thái lịch chiếu
     * @return Danh sách phim có trạng thái tương ứng
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE ss.status = :status " +
            "ORDER BY m.name", nativeQuery = true)
    List<group6.cinema_project.entity.Movie> findMoviesByScheduleStatusNative(@Param("status") String status);

    /**
     * Tìm lịch chiếu theo trạng thái kèm thông tin phim (tối ưu hiệu suất với JOIN
     * FETCH)
     *
     * @param status Trạng thái lịch chiếu
     * @return Danh sách lịch chiếu với thông tin phim
     */
    @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
            "LEFT JOIN FETCH ss.movie m " +
            "WHERE ss.status = :status " +
            "ORDER BY m.name")
    List<ScreeningSchedule> findSchedulesByStatusWithMovie(@Param("status") String status);

    /**
     * Tìm lịch chiếu theo ID phim kèm thông tin liên quan
     *
     * @param movieId ID của phim
     * @return Danh sách lịch chiếu của phim với thông tin đầy đủ
     */
    @Query("SELECT ss FROM ScreeningSchedule ss " +
            "LEFT JOIN FETCH ss.movie m " +
            "LEFT JOIN FETCH ss.screeningRoom sr " +
            "LEFT JOIN FETCH ss.branch b " +
            "WHERE ss.movie.id = :movieId " +
            "ORDER BY ss.screeningDate, ss.startTime")
    List<ScreeningSchedule> findByMovieIdWithRelatedEntities(@Param("movieId") Integer movieId);

    /**
     * Tìm tất cả lịch chiếu trong cùng phòng chiếu và ngày chiếu
     * loại trừ lịch chiếu hiện tại (dùng cho cập nhật)
     *
     * @param screeningRoomId ID phòng chiếu
     * @param screeningDate   Ngày chiếu
     * @param excludeId       ID lịch chiếu cần loại trừ
     * @return Danh sách lịch chiếu trong cùng phòng và ngày
     */
    @Query("SELECT ss FROM ScreeningSchedule ss " +
            "WHERE ss.screeningRoom.id = :screeningRoomId " +
            "AND ss.screeningDate = :screeningDate " +
            "AND (:excludeId IS NULL OR ss.id != :excludeId)")
    List<ScreeningSchedule> findByScreeningRoomAndDateExcludingId(
            @Param("screeningRoomId") Integer screeningRoomId,
            @Param("screeningDate") LocalDate screeningDate,
            @Param("excludeId") Integer excludeId);

    /**
     * Tìm lịch chiếu bị trùng lặp thời gian trong cùng phòng chiếu và ngày
     * Sử dụng Native SQL để xử lý kiểu dữ liệu TIME của SQL Server
     *
     * @param screeningRoomId ID phòng chiếu
     * @param screeningDate   Ngày chiếu
     * @param startTime       Thời gian bắt đầu
     * @param endTime         Thời gian kết thúc
     * @param excludeId       ID lịch chiếu cần loại trừ
     * @return Danh sách lịch chiếu bị trùng thời gian
     */
    @Query(value = "SELECT * FROM ScreeningSchedule ss " +
            "WHERE ss.screening_room_id = :screeningRoomId " +
            "AND ss.screening_date = :screeningDate " +
            "AND (:excludeId IS NULL OR ss.id != :excludeId) " +
            "AND ((ss.start_time < :endTime AND ss.end_time > :startTime))", nativeQuery = true)
    List<ScreeningSchedule> findOverlappingSchedules(
            @Param("screeningRoomId") Integer screeningRoomId,
            @Param("screeningDate") LocalDate screeningDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("excludeId") Integer excludeId);

    /**
     * Tìm phim đang chiếu sử dụng logic kết hợp (trạng thái thủ công + tính toán
     * động)
     * Một phim được coi là đang chiếu nếu có ít nhất một lịch chiếu:
     * - Trạng thái thủ công là 'ACTIVE' HOẶC
     * - Trạng thái là null/AUTO và được tính toán động là đang hoạt động (đã bắt
     * đầu nhưng chưa kết thúc)
     *
     * @return Danh sách phim đang chiếu
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE (ss.status = 'ACTIVE') " +
            "OR (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "(ss.screening_date < CAST(GETDATE() AS DATE)) " +
            "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.start_time <= CAST(GETDATE() AS TIME)) "
            +
            ") AND (" +
            "(ss.screening_date > CAST(GETDATE() AS DATE)) " +
            "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.end_time >= CAST(GETDATE() AS TIME))" +
            ") " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findCurrentlyPlayingMovies();

    /**
     * Tìm phim sắp chiếu sử dụng logic đơn giản
     * Một phim được coi là sắp chiếu nếu có ít nhất một lịch chiếu:
     * - Trạng thái thủ công là 'UPCOMING' HOẶC
     * - Trạng thái là null/AUTO và được tính toán động là sắp tới (chưa bắt đầu)
     * LƯU Ý: Đã loại bỏ logic loại trừ để cho phép phim xuất hiện trong nhiều tab
     *
     * @return Danh sách phim sắp chiếu
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE ((ss.status = 'UPCOMING') " +
            "OR (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "(ss.screening_date > CAST(GETDATE() AS DATE)) " +
            "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.start_time > CAST(GETDATE() AS TIME))" +
            ")) " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findComingSoonMovies();

    /**
     * Tìm phim đã ngừng chiếu sử dụng logic kết hợp (trạng thái thủ công + tính
     * toán động)
     * Một phim được coi là đã ngừng chiếu nếu TẤT CẢ lịch chiếu của nó:
     * - Trạng thái thủ công là 'ENDED' hoặc 'CANCELLED' HOẶC
     * - Trạng thái là null/AUTO và được tính toán động là đã kết thúc (tất cả suất
     * chiếu đã hoàn thành)
     *
     * @return Danh sách phim đã ngừng chiếu
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "WHERE m.id IN (SELECT DISTINCT ss.movie_id FROM ScreeningSchedule ss) " +
            "AND m.id NOT IN (" +
            "SELECT DISTINCT ss.movie_id FROM ScreeningSchedule ss " +
            "WHERE (ss.status = 'ACTIVE' OR ss.status = 'UPCOMING') " +
            "OR (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "(ss.screening_date > CAST(GETDATE() AS DATE)) " +
            "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.end_time >= CAST(GETDATE() AS TIME))" +
            ")" +
            ") " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findStoppedShowingMovies();

    /**
     * Tìm phim có ít nhất một lịch chiếu với trạng thái 'ENDED'
     * Đây là cách đơn giản và trực quan hơn so với logic phức tạp ở trên
     *
     * @return Danh sách phim có lịch chiếu đã kết thúc
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE ss.status = 'ENDED' " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findMoviesWithEndedSchedules();

    /**
     * Tìm phim có ít nhất một lịch chiếu với trạng thái 'ACTIVE'
     * Đây là cách đơn giản và trực quan hơn so với logic phức tạp ở trên
     *
     * @return Danh sách phim có lịch chiếu đang hoạt động
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE ss.status = 'ACTIVE' " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findMoviesWithActiveSchedules();

    /**
     * Tìm tất cả lịch chiếu đã kết thúc nhưng vẫn có trạng thái ACTIVE
     * Một lịch chiếu được coi là đã kết thúc nếu:
     * - Ngày chiếu đã qua, hoặc
     * - Ngày chiếu là hôm nay và thời gian kết thúc đã qua
     */
    @Query(value = "SELECT * FROM ScreeningSchedule ss " +
            "WHERE ss.status = 'ACTIVE' " +
            "AND ((ss.screening_date < CAST(GETDATE() AS DATE)) " +
            "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.end_time < CAST(GETDATE() AS TIME)))", nativeQuery = true)
    List<ScreeningSchedule> findExpiredActiveSchedules();

    /**
     * Tìm tất cả lịch chiếu đã đến thời gian chiếu nhưng vẫn có trạng thái UPCOMING
     * Một lịch chiếu đã đến thời gian chiếu nếu:
     * - Ngày chiếu đã qua, hoặc
     * - Ngày chiếu là hôm nay và thời gian bắt đầu đã qua
     */
    @Query(value = "SELECT * FROM ScreeningSchedule ss " +
            "WHERE ss.status = 'UPCOMING' " +
            "AND (" +
            "    (ss.screening_date < CONVERT(DATE, GETDATE())) " +
            "    OR " +
            "    (ss.screening_date = CONVERT(DATE, GETDATE()) AND ss.start_time <= CONVERT(TIME, GETDATE()))"
            +
            ")", nativeQuery = true)
    List<ScreeningSchedule> findUpcomingSchedulesThatShouldBeActive();

    /**
     * Tìm lịch chiếu theo ID phim và trạng thái với thông tin liên quan
     */
    @Query("SELECT ss FROM ScreeningSchedule ss " +
            "LEFT JOIN FETCH ss.movie m " +
            "LEFT JOIN FETCH ss.screeningRoom sr " +
            "LEFT JOIN FETCH ss.branch b " +
            "WHERE ss.movie.id = :movieId " +
            "AND ss.status = :status " +
            "ORDER BY ss.screeningDate, ss.startTime")
    List<ScreeningSchedule> findByMovieIdAndStatusWithRelatedEntities(
            @Param("movieId") Integer movieId,
            @Param("status") String status);

    /**
     * Tìm lịch chiếu đã kết thúc cho một phim cụ thể sử dụng logic động
     * Một lịch chiếu được coi là đã kết thúc nếu:
     * - Manual status là 'ENDED' hoặc 'CANCELLED' HOẶC
     * - Status là null/'AUTO' và đã kết thúc theo thời gian thực tế
     */
    @Query(value = "SELECT ss.*, m.name as movie_name, m.image as movie_image, " +
            "sr.name as ScreeningSchedule, b.name as branch_name " +
            "FROM ScreeningSchedule ss " +
            "LEFT JOIN Movie m ON ss.movie_id = m.id " +
            "LEFT JOIN ScreeningRoom sr ON ss.screening_room_id = sr.id " +
            "LEFT JOIN Branch b ON ss.branch_id = b.id " +
            "WHERE ss.movie_id = :movieId " +
            "AND (" +
            "  (ss.status = 'ENDED' OR ss.status = 'CANCELLED') " +
            "  OR " +
            "  (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "    (ss.screening_date < CAST(GETDATE() AS DATE)) " +
            "    OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.end_time < CAST(GETDATE() AS TIME))"
            +
            "  )" +
            ") " +
            "ORDER BY ss.screening_date, ss.start_time", nativeQuery = true)
    List<ScreeningSchedule> findEndedSchedulesByMovieIdWithRelatedEntities(@Param("movieId") Integer movieId);

    /**
     * Tìm lịch chiếu đang hoạt động cho một phim cụ thể sử dụng logic động
     * Một lịch chiếu được coi là đang hoạt động nếu:
     * - Manual status là 'ACTIVE' HOẶC
     * - Status là null/'AUTO' và đang trong thời gian chiếu
     */
    @Query(value = "SELECT ss.*, m.name as movie_name, m.image as movie_image, " +
            "sr.name as screening_room_name, b.name as branch_name " +
            "FROM ScreeningSchedule ss " +
            "LEFT JOIN Movie m ON ss.movie_id = m.id " +
            "LEFT JOIN ScreeningRoom sr ON ss.screening_room_id = sr.id " +
            "LEFT JOIN Branch b ON ss.branch_id = b.id " +
            "WHERE ss.movie_id = :movieId " +
            "AND (" +
            "  ss.status = 'ACTIVE' " +
            "  OR " +
            "  (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "    (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.start_time <= CAST(GETDATE() AS TIME) AND ss.end_time >= CAST(GETDATE() AS TIME)) "
            +
            "    OR (ss.screening_date < CAST(GETDATE() AS DATE) AND ss.screening_date >= DATEADD(day, -1, CAST(GETDATE() AS DATE)))"
            +
            "  )" +
            ") " +
            "ORDER BY ss.screening_date, ss.start_time", nativeQuery = true)
    List<ScreeningSchedule> findActiveSchedulesByMovieIdWithRelatedEntities(@Param("movieId") Integer movieId);

    /**
     * Tìm lịch chiếu sắp tới cho một phim cụ thể sử dụng logic động
     * Một lịch chiếu được coi là sắp tới nếu:
     * - Manual status là 'UPCOMING' HOẶC
     * - Status là null/'AUTO' và chưa đến thời gian chiếu
     */
    @Query(value = "SELECT ss.*, m.name as movie_name, m.image as movie_image, " +
            "sr.name as screening_room_name, b.name as branch_name " +
            "FROM ScreeningSchedule ss " +
            "LEFT JOIN Movie m ON ss.movie_id = m.id " +
            "LEFT JOIN ScreeningRoom sr ON ss.screening_room_id = sr.id " +
            "LEFT JOIN Branch b ON ss.branch_id = b.id " +
            "WHERE ss.movie_id = :movieId " +
            "AND (" +
            "  ss.status = 'UPCOMING' " +
            "  OR " +
            "  (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
            "    (ss.screening_date > CAST(GETDATE() AS DATE)) " +
            "    OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.start_time > CAST(GETDATE() AS TIME))"
            +
            "  )" +
            ") " +
            "ORDER BY ss.screening_date, ss.start_time", nativeQuery = true)
    List<ScreeningSchedule> findUpcomingSchedulesByMovieIdWithRelatedEntities(@Param("movieId") Integer movieId);

    /**
     * Find movies that have at least one schedule with 'UPCOMING' status
     * Simple query without exclusion logic - allows movies to appear in multiple
     * tabs
     */
    @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
            "INNER JOIN ScreeningSchedule ss ON m.id = ss.movie_id " +
            "WHERE ss.status = 'UPCOMING' " +
            "ORDER BY m.name", nativeQuery = true)
    List<Movie> findMoviesWithUpcomingSchedules();
}
