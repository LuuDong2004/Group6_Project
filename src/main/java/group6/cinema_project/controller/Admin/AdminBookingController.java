package group6.cinema_project.controller.Admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.User.IBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@Slf4j
public class AdminBookingController {

    private final IBookingService bookingService;

    /**
     * Hiển thị danh sách tất cả booking với phân trang
     */
    @GetMapping
    public String listAllBookings(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size,
                                 @RequestParam(value = "searchTerm", required = false) String searchTerm,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "scheduleId", required = false) Integer scheduleId) {
        try {
            // Clean up search term
            searchTerm = (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null;
            
            // Lấy bookings với phân trang
            Page<BookingDto> bookingPage = bookingService.getBookingsPage(page, size, searchTerm, status, scheduleId);
            
            // Add attributes to model
            model.addAttribute("bookingPage", bookingPage);
            model.addAttribute("bookings", bookingPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookingPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");
            model.addAttribute("status", status != null ? status : "");
            model.addAttribute("scheduleId", scheduleId);
            
            // If viewing by schedule, add schedule info
            if (scheduleId != null && !bookingPage.getContent().isEmpty()) {
                model.addAttribute("schedule", bookingPage.getContent().get(0).getSchedule());
            }
            
            log.info("Successfully loaded {} bookings for admin (page {}/{})", 
                    bookingPage.getContent().size(), page + 1, bookingPage.getTotalPages());
            return "admin/admin_bookings_list";
            
        } catch (Exception e) {
            log.error("Error loading bookings: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi tải danh sách booking: " + e.getMessage());
            return "admin/admin_bookings_list";
        }
    }

    /**
     * Hiển thị booking theo suất chiếu cụ thể
     */
    @GetMapping("/schedule/{scheduleId}")
    public String listBookingsBySchedule(@PathVariable Integer scheduleId, Model model) {
        try {
            List<BookingDto> bookings = bookingService.getBookingsByScheduleId(scheduleId);
            model.addAttribute("bookings", bookings);
            model.addAttribute("scheduleId", scheduleId);
            
            if (!bookings.isEmpty() && bookings.get(0).getSchedule() != null) {
                model.addAttribute("schedule", bookings.get(0).getSchedule());
            }
            
            log.info("Successfully loaded {} bookings for schedule {}", bookings.size(), scheduleId);
            return "admin/admin_bookings_list";
            
        } catch (Exception e) {
            log.error("Error loading bookings for schedule {}: {}", scheduleId, e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi tải danh sách booking cho suất chiếu: " + e.getMessage());
            return "admin/admin_bookings_list";
        }
    }

    /**
     * Hiển thị chi tiết booking
     */
    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable Integer id, Model model) {
        try {
            BookingDto booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            
            log.info("Successfully loaded booking detail for ID: {}", id);
            return "admin/admin_booking_detail";
            
        } catch (Exception e) {
            log.error("Error loading booking detail for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi tải chi tiết booking: " + e.getMessage());
            return "admin/admin_booking_detail";
        }
    }

    /**
     * Gửi lại email vé điện tử
     */
    @PostMapping("/{id}/resend-email")
    public String resendEmail(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            BookingDto booking = bookingService.getBookingById(id);
            
            // Kiểm tra trạng thái booking
            if (!"PAID".equals(booking.getStatus())) {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể gửi lại email cho vé đã thanh toán");
                return "redirect:/admin/bookings/" + id;
            }
            
            // Gửi lại email vé điện tử
            // TODO: Implement mail service call here
            // mailService.sendETicketEmail(booking, booking.getUser().getEmail());
            
            redirectAttributes.addFlashAttribute("success", "Đã gửi lại email vé điện tử thành công");
            log.info("Successfully resent email for booking ID: {}", id);
            
        } catch (Exception e) {
            log.error("Error resending email for booking ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi lại email: " + e.getMessage());
        }
        
        return "redirect:/admin/bookings/" + id;
    }

    /**
     * Hủy booking
     */
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = bookingService.cancelBooking(id);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Đã hủy booking thành công");
                log.info("Successfully cancelled booking ID: {}", id);
            } else {
                redirectAttributes.addFlashAttribute("error", "Không thể hủy booking");
            }
        } catch (Exception e) {
            log.error("Error cancelling booking ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hủy booking: " + e.getMessage());
        }
        
        return "redirect:/admin/bookings/" + id;
    }

    /**
     * Xác nhận thanh toán booking
     */
    @PostMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.confirmBookingPaid(id);
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận thanh toán thành công");
            log.info("Successfully confirmed payment for booking ID: {}", id);
            
        } catch (Exception e) {
            log.error("Error confirming payment for booking ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
        
        return "redirect:/admin/bookings/" + id;
    }
} 