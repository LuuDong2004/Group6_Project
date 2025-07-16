package group6.cinema_project.repository;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s " +
           "JOIN s.room r " +
           "JOIN ScreeningSchedule sch ON sch.screeningRoom.id = r.id " +
           "WHERE sch.id = :scheduleId")
    List<Seat> findSeatsByRoomId(@Param("scheduleId") Integer scheduleId);

}