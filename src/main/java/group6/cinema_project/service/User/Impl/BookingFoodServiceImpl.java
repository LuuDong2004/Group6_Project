package group6.cinema_project.service.User.Impl;

import group6.cinema_project.entity.BookingFood;
import group6.cinema_project.repository.User.BookingFoodRepository;
import group6.cinema_project.service.User.IBookingFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BookingFoodServiceImpl implements IBookingFoodService {
    @Autowired
    private BookingFoodRepository bookingFoodRepository;

    @Override
    public List<BookingFood> getBookingFoodByBookingId(Integer bookingId) {
        return bookingFoodRepository.findByBookingId(bookingId);
    }

    @Override
    public BookingFood save(BookingFood bookingFood) {

        return bookingFoodRepository.save(bookingFood);
    }
} 