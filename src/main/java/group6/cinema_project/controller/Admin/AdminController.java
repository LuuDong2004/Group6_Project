package group6.cinema_project.controller.Admin;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import group6.cinema_project.repository.User.BookingRepository;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.repository.User.ScreeningScheduleRepository;
import group6.cinema_project.repository.User.VoucherRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningScheduleRepository screeningScheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/secret-login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Nếu đã đăng nhập, redirect về dashboard
            return "redirect:/admin";
        }
        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }
        return "admin/admin_secret_login";
    }

    @GetMapping({"", "/dashboard"})
    public String adminDashboard(Model model) {
        // Thống kê phim đang chiếu
        long totalMovies = movieRepository.count();
        model.addAttribute("totalMovies", totalMovies);

        // Thống kê tổng số suất chiếu
        long totalSchedules = screeningScheduleRepository.count();
        model.addAttribute("todaySchedules", totalSchedules);

        // Thống kê vé đã bán
        long totalBookings = bookingRepository.count();
        model.addAttribute("totalBookings", totalBookings);

        // Thống kê doanh thu hôm nay
        double todayRevenue = bookingRepository.findAll().stream()
                .filter(booking -> booking.getDate() != null && booking.getDate().equals(LocalDate.now()))
                .mapToDouble(booking -> booking.getAmount())
                .sum();
        model.addAttribute("todayRevenue", String.format("%.0f", todayRevenue));

        // Thống kê voucher
        long totalVouchers = voucherRepository.count();
        long activeVouchers = voucherRepository.findAll().stream()
                .filter(voucher -> "ACTIVE".equals(voucher.getStatus()) &&
                        voucher.getExpiryDate().isAfter(LocalDate.now().minusDays(1)))
                .count();
        model.addAttribute("totalVouchers", totalVouchers);
        model.addAttribute("activeVouchers", activeVouchers);

        return "admin/admin_dashboard";
    }


}