package group6.cinema_project.controller.User;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.BookingRequest;
import group6.cinema_project.service.User.IBookingService;
import group6.cinema_project.service.User.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.entity.User;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            // Lấy userId từ user đang đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Bạn cần đăng nhập để đặt vé");
            }
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với email: " + email));
            request.setUserId(user.getId());
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
     * Gửi lại email vé điện tử
     */
    @PostMapping("/resend-email/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> resendETicketEmail(@PathVariable Integer bookingId) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            
            // Kiểm tra trạng thái booking
            if (!"PAID".equals(booking.getStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể gửi lại email cho vé đã thanh toán");
            }
            
            // Gửi lại email vé điện tử
            mailService.sendETicketEmail(booking, booking.getUser().getEmail());
            
            return ResponseEntity.ok().body("Đã gửi lại email vé điện tử thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi gửi lại email: " + e.getMessage());
        }
    }

    /**
     * Trang hiển thị form đặt vé
     */
    @GetMapping("/form")
    public String showBookingForm() {
        return "booking-form";
    }


    @GetMapping("/list")
    public String showBookingList() {
        return "booking-list";
    }

    @GetMapping("/detail/{id}")
    public String bookingDetail(@PathVariable Integer id, Model model) {
        BookingDto booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        return "booking_detail";
    }
}
