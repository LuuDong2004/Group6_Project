package group6.cinema_project.repository;

import group6.cinema_project.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s.screeningTimeSlot.slot.startTime " +
            "FROM Schedule s " +
            "WHERE s.movie.id = :movieId " +
            "AND s.branch.id = :branchId")
    List<LocalTime> getStartTimeByMovieIdAndBranchId(
            @Param("movieId") Integer movieId,
            @Param("branchId") Integer branchId);


    @Query("SELECT s FROM Schedule s WHERE s.movie.id = :movieId")
    List<Schedule> findSchedulesByMovieId(@Param("movieId") Integer movieId);



}
