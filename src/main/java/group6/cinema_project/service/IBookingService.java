package group6.cinema_project.service;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;

import java.util.List;

public interface IBookingService {

    List<BookingDto> createBooking(BookingRequest request);
    List<BookingDto> getBookingsByUserId(Integer userId);
    boolean cancelBooking(Integer bookingId);
    BookingDto getBookingById(Integer bookingId);
    boolean updateBookingStatus(Integer bookingId, String status);
    void confirmBookingPaid(int bookingId);
}
