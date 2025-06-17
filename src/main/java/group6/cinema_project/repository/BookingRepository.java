package group6.cinema_project.repository;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);

    Integer user(User user);


}
