package group6.cinema_project.repository;

import group6.cinema_project.entity.Ticket;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository <Ticket, Integer>{

    @Query("SELECT t FROM Ticket t WHERE t.screeningSchedule.id = :scheduleId")
    List<Ticket> findTicketsByScreeningScheduleId(@Param("scheduleId") int scheduleId);

//    List<Ticket> findTicketsByScreeningScheduleIdAndSeatId(int screeningScheduleId, int seatId);

    @Query("SELECT t FROM Ticket t WHERE t.invoice.user.id = :customerId")
    List<Ticket> findTicketsByCustomerId(@Param("customerId") int customerId);
}
