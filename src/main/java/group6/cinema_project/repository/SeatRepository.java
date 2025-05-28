package group6.cinema_project.repository;

import group6.cinema_project.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findSeatsByScreeningRoomId(Integer room ); // tham số truyền vào phải đúng trong entity

}

