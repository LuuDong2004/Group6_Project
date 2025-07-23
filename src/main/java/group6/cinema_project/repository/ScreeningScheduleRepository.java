package group6.cinema_project.repository;



import group6.cinema_project.entity.ScreeningSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningScheduleRepository extends JpaRepository<ScreeningSchedule, Long> {
}
