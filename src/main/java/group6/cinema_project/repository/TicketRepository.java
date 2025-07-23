package group6.cinema_project.repository;


import group6.cinema_project.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t WHERE t.screeningSchedule.id = :scheduleId")
    List<Ticket> findTicketsByScreeningScheduleId(@Param("scheduleId") Long scheduleId);
}