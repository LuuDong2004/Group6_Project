package group6.cinema_project.repository.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);

    List<Booking> findByUserIdAndDateAfterAndStatus(Integer userId, LocalDate fromDate, String status);
    
    // Admin methods - sắp xếp theo ngày đặt giảm dần (mới nhất trước)
    @Query("SELECT b FROM Booking b ORDER BY b.date DESC")
    List<Booking> findAllOrderByDateDesc();
    
    @Query("SELECT b FROM Booking b WHERE b.schedule.id = :scheduleId ORDER BY b.date DESC")
    List<Booking> findByScheduleIdOrderByDateDesc(Integer scheduleId);
}
