package group6.cinema_project.service.User;

import java.util.List;

import org.springframework.data.domain.Page;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;

public interface IBookingService {
    List<BookingDto> createBooking(BookingRequest request);
    List<BookingDto> getBookingsByUserId(Integer userId);
    List<BookingDto> getPaidBookingsByUserIdAndDateAfter(Integer userId, java.time.LocalDate fromDate);
    boolean cancelBooking(Integer bookingId);
    BookingDto getBookingById(Integer bookingId);
    boolean updateBookingStatus(Integer bookingId, String status);
    void confirmBookingPaid(int bookingId);
    void cancelPendingBooking(Integer bookingId);
    List<BookingDto> getPaidBookingsByUserIdAndDateAfterSortedByShowDateDesc(Integer userId, java.time.LocalDate fromDate);
    void updateBookingAmount(Integer bookingId, double newAmount);
    
    // Admin methods
    List<BookingDto> getAllBookings();
    List<BookingDto> getBookingsByScheduleId(Integer scheduleId);
    
    // Pagination methods
    Page<BookingDto> getBookingsPage(int page, int size, String searchTerm, String status, Integer scheduleId);
}
