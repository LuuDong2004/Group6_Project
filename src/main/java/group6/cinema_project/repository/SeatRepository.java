package group6.cinema_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group6.cinema_project.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId")
    List<Seat> findSeatsByScreeningRoomId(@Param("roomId") Integer roomId);

 //   List<Seat> findByScreeningRoomId(Integer screeningRoomId);

}

