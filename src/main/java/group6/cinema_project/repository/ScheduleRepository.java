package group6.cinema_project.repository;

import group6.cinema_project.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findSchedulesByMovieId(@Param("movieId") Integer movieId);

    @Query("SELECT s FROM Schedule s WHERE s.movie.id = :movieId AND s.screeningDate = :screeningDate")
    List<Schedule> findSchedulesByMovieIdAndDate(@Param("movieId") Integer movieId,
                                                 @Param("screeningDate") Date screeningDate);

    @Query("SELECT s FROM Schedule s WHERE s.movie.id = :movieId AND s.branch.id = :branchId AND s.screeningDate = :screeningDate")
    List<Schedule> findSchedulesByMovieIdAndBranchIdAndDate(@Param("movieId") Integer movieId,
                                                            @Param("branchId") Integer branchId,
                                                            @Param("screeningDate") Date screeningDate);

    @Query("SELECT DISTINCT s.branch FROM Schedule s WHERE s.movie.id = :movieId")
    List<Object> findDistinctBranchesByMovieId(@Param("movieId") Integer movieId);

    @Query("SELECT s FROM Schedule s WHERE s.id = :scheduleId")
    Schedule findScheduleById(@Param("scheduleId") Integer scheduleId);

    // Sửa kiểu trả về và bỏ hàm DATE() - phương thức gốc
    @Query("SELECT DISTINCT s.screeningDate FROM Schedule s WHERE s.movie.id = :movieId ORDER BY s.screeningDate")
    List<Date> findDistinctScreeningDatesByMovieId(@Param("movieId") Integer movieId);

    // Lấy các ngày có lịch chiếu từ ngày hiện tại trở đi
    @Query("SELECT DISTINCT s.screeningDate FROM Schedule s WHERE s.movie.id = :movieId AND s.screeningDate >= :currentDate ORDER BY s.screeningDate")
    List<Date> findDistinctScreeningDatesByMovieIdFromDate(@Param("movieId") Integer movieId, @Param("currentDate") Date currentDate);

}