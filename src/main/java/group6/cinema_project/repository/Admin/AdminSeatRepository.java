package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminSeatRepository extends JpaRepository<Seat, Integer> {
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId")
    List<Seat> findSeatsByScreeningRoomId(@Param("roomId") Integer roomId);
}
