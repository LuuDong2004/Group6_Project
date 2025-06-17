package group6.cinema_project.controller;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;
import group6.cinema_project.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    /**
     * Tạo booking mới
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            List<BookingDto> booking = bookingService.createBooking(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo booking: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách booking của người dùng
     */
    @GetMapping("/user/{userId}")
    @ResponseBody
    public ResponseEntity<?> getBookingsByUserId(@PathVariable Integer userId) {
        try {
            List<BookingDto> bookings = bookingService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách booking: " + e.getMessage());
        }
    }

    /**
     * Hủy booking
     */
    @PostMapping("/cancel/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId) {
        try {
            boolean success = bookingService.cancelBooking(bookingId);
            return success ? 
                ResponseEntity.ok().body("Hủy booking thành công") : 
                ResponseEntity.badRequest().body("Không thể hủy booking");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi hủy booking: " + e.getMessage());
        }
    }

    /**
     * Trang hiển thị form đặt vé
     */
    @GetMapping("/form")
    public String showBookingForm() {
        return "booking-form";
    }

    /**
     * Trang hiển thị danh sách booking của người dùng
     */
    @GetMapping("/list")
    public String showBookingList() {
        return "booking-list";
    }
}
