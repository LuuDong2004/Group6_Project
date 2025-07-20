package group6.cinema_project.service.User;

import group6.cinema_project.entity.BookingFood;
import java.util.List;
 
public interface IBookingFoodService {
    List<BookingFood> getBookingFoodByBookingId(Integer bookingId);
    BookingFood save(BookingFood bookingFood);
} 