package group6.cinema_project.repository.Admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group6.cinema_project.entity.Seat;

public interface AdminSeatRepository extends JpaRepository<Seat, Integer> {
    // Tìm tất cả ghế theo ID phòng chiếu
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId")
    List<Seat> findSeatsByScreeningRoomId(@Param("roomId") Integer roomId);
    
    // Kiểm tra xem ghế có đang được đặt không
    @Query("SELECT COUNT(sr) > 0 FROM group6.cinema_project.entity.SeatReservation sr WHERE sr.seat.id = :seatId")
    boolean hasReservations(@Param("seatId") Integer seatId);
}
