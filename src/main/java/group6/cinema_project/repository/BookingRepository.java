package group6.cinema_project.repository;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.dto.StatisticDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT u.user_name as userName, m.name as movieName, COUNT(t.id) as totalTickets, SUM(t.price) as totalAmount " +
            "FROM Users u " +
            "JOIN Booking b ON u.id = b.user_id " +
            "JOIN Ticket t ON t.invoice_id = b.id " +
            "JOIN ScreeningSchedule s ON b.screening_schedule_id = s.id " +
            "JOIN Movie m ON s.movie_id = m.id " +
            "GROUP BY u.user_name, m.name", nativeQuery = true)
    List<Object[]> getStatisticsRaw();
}