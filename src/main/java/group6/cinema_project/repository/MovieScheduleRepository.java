package group6.cinema_project.repository;

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
}
