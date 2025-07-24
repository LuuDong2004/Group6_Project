package group6.cinema_project.repository;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);

    Integer user(User user);

    /**
     * Kiểm tra xem có booking nào cho schedule cụ thể chưa
     * 
     * @param scheduleId ID của lịch chiếu
     * @return true nếu có booking, false nếu không có
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.schedule.id = :scheduleId")
    boolean existsByScheduleId(@Param("scheduleId") Integer scheduleId);

    /**
     * Đếm số lượng booking cho một schedule cụ thể
     * 
     * @param scheduleId ID của lịch chiếu
     * @return số lượng booking
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.id = :scheduleId")
    long countByScheduleId(@Param("scheduleId") Integer scheduleId);
}
