package group6.cinema_project.repository.User;

import group6.cinema_project.entity.BookingFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingFoodRepository extends JpaRepository<BookingFood, Integer> {
    List<BookingFood> findByBookingId(Integer bookingId);
} 