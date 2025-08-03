package group6.cinema_project.repository.User;

import org.springframework.data.jpa.repository.JpaRepository;

import group6.cinema_project.entity.ScreeningSchedule;

public interface ScreeningScheduleRepository extends JpaRepository<ScreeningSchedule, Long> {

    // Repository này sử dụng method count() có sẵn từ JpaRepository
}
