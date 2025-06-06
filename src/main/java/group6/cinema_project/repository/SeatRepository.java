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

    // Sửa lỗi tên method - thiếu phần cuối
    List<Seat> findSeatsByRoomId(Integer roomId);

    // Hoặc có thể dùng custom query để có thêm thông tin về trạng thái ghế
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId ORDER BY s.row, s.name")
    List<Seat> findSeatsByRoomIdOrderByRowAndNumber(@Param("roomId") Integer roomId);

    // Query để kiểm tra ghế đã đặt cho một lịch chiếu cụ thể
    @Query("SELECT s FROM Seat s WHERE s.room.id = :roomId AND s.id IN " +
            "(SELECT t.seat.id FROM Ticket t WHERE t.schedule.id = :scheduleId)")
    List<Seat> findOccupiedSeatsByRoomAndSchedule(@Param("roomId") Integer roomId,
                                                  @Param("scheduleId") Integer scheduleId);
}