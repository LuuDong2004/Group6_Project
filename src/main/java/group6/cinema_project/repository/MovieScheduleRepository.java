package group6.cinema_project.repository;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningSchedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieScheduleRepository extends JpaRepository<ScreeningSchedule, Integer> {

        List<ScreeningSchedule> getScreeningSchedulesByMovieId(Integer movieId);

        @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
                        "LEFT JOIN FETCH ss.movie m " +
                        "LEFT JOIN FETCH ss.screeningRoom sr " +
                        "LEFT JOIN FETCH ss.branch b " +
                        "ORDER BY m.name, ss.screeningDate, ss.startTime")
        List<ScreeningSchedule> findAllWithRelatedEntities();

        @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
                        "LEFT JOIN FETCH ss.movie m " +
                        "LEFT JOIN FETCH ss.screeningRoom sr " +
                        "LEFT JOIN FETCH ss.branch b " +
                        "WHERE (:movieId IS NULL OR ss.movieId = :movieId) " +
                        "AND (:screeningDate IS NULL OR ss.screeningDate = :screeningDate) " +
                        "AND (:screeningRoomId IS NULL OR ss.screeningRoomId = :screeningRoomId) " +
                        "ORDER BY m.name, ss.screeningDate, ss.startTime")
        List<ScreeningSchedule> findFilteredWithRelatedEntities(
                        @Param("movieId") Integer movieId,
                        @Param("screeningDate") LocalDate screeningDate,
                        @Param("screeningRoomId") Integer screeningRoomId);

        @Query("SELECT DISTINCT m FROM ScreeningSchedule ss " +
                        "JOIN ss.movie m " +
                        "WHERE ss.status = :status " +
                        "ORDER BY m.name")
        List<group6.cinema_project.entity.Movie> findMoviesByScheduleStatus(@Param("status") String status);

        // Alternative native SQL query as fallback
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "INNER JOIN screening_schedule ss ON m.id = ss.movie_id " +
                        "WHERE ss.status = :status " +
                        "ORDER BY m.name", nativeQuery = true)
        List<group6.cinema_project.entity.Movie> findMoviesByScheduleStatusNative(@Param("status") String status);

        // Additional backup query with explicit JOIN FETCH for better performance
        @Query("SELECT DISTINCT ss FROM ScreeningSchedule ss " +
                        "LEFT JOIN FETCH ss.movie m " +
                        "WHERE ss.status = :status " +
                        "ORDER BY m.name")
        List<ScreeningSchedule> findSchedulesByStatusWithMovie(@Param("status") String status);

        @Query("SELECT ss FROM ScreeningSchedule ss " +
                        "LEFT JOIN FETCH ss.movie m " +
                        "LEFT JOIN FETCH ss.screeningRoom sr " +
                        "LEFT JOIN FETCH ss.branch b " +
                        "WHERE ss.movieId = :movieId " +
                        "ORDER BY ss.screeningDate, ss.startTime")
        List<ScreeningSchedule> findByMovieIdWithRelatedEntities(@Param("movieId") Integer movieId);

        /**
         * Find all screening schedules in the same screening room on the same date
         * excluding the current schedule (for updates)
         */
        @Query("SELECT ss FROM ScreeningSchedule ss " +
                        "WHERE ss.screeningRoomId = :screeningRoomId " +
                        "AND ss.screeningDate = :screeningDate " +
                        "AND (:excludeId IS NULL OR ss.id != :excludeId)")
        List<ScreeningSchedule> findByScreeningRoomAndDateExcludingId(
                        @Param("screeningRoomId") Integer screeningRoomId,
                        @Param("screeningDate") LocalDate screeningDate,
                        @Param("excludeId") Integer excludeId);

        /**
         * Find overlapping schedules in the same screening room on the same date
         * that conflict with the given time range
         * Using native SQL to handle SQL Server TIME data type properly
         */
        @Query(value = "SELECT * FROM screening_schedule ss " +
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
         * Find movies that are currently playing using hybrid logic (manual status +
         * dynamic calculation)
         * A movie is currently playing if it has at least one schedule where:
         * - Manual status is 'ACTIVE' OR
         * - Status is null/AUTO and dynamically calculated as active (started but not
         * ended)
         */
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "INNER JOIN screening_schedule ss ON m.id = ss.movie_id " +
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
         * Find movies that are coming soon using hybrid logic (manual status + dynamic
         * calculation)
         * A movie is coming soon if it has at least one schedule where:
         * - Manual status is 'UPCOMING' OR
         * - Status is null/AUTO and dynamically calculated as upcoming (not started
         * yet)
         * AND the movie is not currently playing
         */
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "INNER JOIN screening_schedule ss ON m.id = ss.movie_id " +
                        "WHERE ((ss.status = 'UPCOMING') " +
                        "OR (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
                        "(ss.screening_date > CAST(GETDATE() AS DATE)) " +
                        "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.start_time > CAST(GETDATE() AS TIME))" +
                        ")) " +
                        "AND m.id NOT IN (" +
                        "SELECT DISTINCT m2.id FROM Movie m2 " +
                        "INNER JOIN screening_schedule ss2 ON m2.id = ss2.movie_id " +
                        "WHERE (ss2.status = 'ACTIVE') " +
                        "OR (ss2.status IS NULL OR ss2.status = 'AUTO') AND (" +
                        "(ss2.screening_date < CAST(GETDATE() AS DATE)) " +
                        "OR (ss2.screening_date = CAST(GETDATE() AS DATE) AND ss2.start_time <= CAST(GETDATE() AS TIME)) "
                        +
                        ") AND (" +
                        "(ss2.screening_date > CAST(GETDATE() AS DATE)) " +
                        "OR (ss2.screening_date = CAST(GETDATE() AS DATE) AND ss2.end_time >= CAST(GETDATE() AS TIME))"
                        +
                        ")" +
                        ") " +
                        "ORDER BY m.name", nativeQuery = true)
        List<Movie> findComingSoonMovies();

        /**
         * Find movies that have stopped showing using hybrid logic (manual status +
         * dynamic calculation)
         * A movie has stopped showing if ALL its schedules are:
         * - Manual status is 'ENDED' or 'CANCELLED' OR
         * - Status is null/AUTO and dynamically calculated as ended (all screenings
         * finished)
         */
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "WHERE m.id IN (SELECT DISTINCT ss.movie_id FROM screening_schedule ss) " +
                        "AND m.id NOT IN (" +
                        "SELECT DISTINCT ss.movie_id FROM screening_schedule ss " +
                        "WHERE (ss.status = 'ACTIVE' OR ss.status = 'UPCOMING') " +
                        "OR (ss.status IS NULL OR ss.status = 'AUTO') AND (" +
                        "(ss.screening_date > CAST(GETDATE() AS DATE)) " +
                        "OR (ss.screening_date = CAST(GETDATE() AS DATE) AND ss.end_time >= CAST(GETDATE() AS TIME))" +
                        ")" +
                        ") " +
                        "ORDER BY m.name", nativeQuery = true)
        List<Movie> findStoppedShowingMovies();

        /**
         * Find movies that have at least one schedule with 'ENDED' status
         * This is simpler and more intuitive than the complex logic above
         */
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "INNER JOIN screening_schedule ss ON m.id = ss.movie_id " +
                        "WHERE ss.status = 'ENDED' " +
                        "ORDER BY m.name", nativeQuery = true)
        List<Movie> findMoviesWithEndedSchedules();

        /**
         * Find movies that have at least one schedule with 'ACTIVE' status
         * This is simpler and more intuitive than the complex logic above
         */
        @Query(value = "SELECT DISTINCT m.* FROM Movie m " +
                        "INNER JOIN screening_schedule ss ON m.id = ss.movie_id " +
                        "WHERE ss.status = 'ACTIVE' " +
                        "ORDER BY m.name", nativeQuery = true)
        List<Movie> findMoviesWithActiveSchedules();

        /**
         * Tìm tất cả lịch chiếu đã kết thúc nhưng vẫn có trạng thái ACTIVE
         * Một lịch chiếu được coi là đã kết thúc nếu:
         * - Ngày chiếu đã qua, hoặc
         * - Ngày chiếu là hôm nay và thời gian kết thúc đã qua
         */
        @Query(value = "SELECT * FROM screening_schedule ss " +
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
        @Query(value = "SELECT * FROM screening_schedule ss " +
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
                        "WHERE ss.movieId = :movieId " +
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
                        "sr.name as screening_room_name, b.name as branch_name " +
                        "FROM screening_schedule ss " +
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
                        "FROM screening_schedule ss " +
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
                        "FROM screening_schedule ss " +
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
      
}
