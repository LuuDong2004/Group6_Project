package group6.cinema_project.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import group6.cinema_project.entity.User;
import group6.cinema_project.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    @GetMapping("/login")
    public String staffLoginPage() {
        return "staff_login";

    }
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public String staffLogin(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Tài khoản staff không tồn tại!");
            return "staff_login";
        }
        User user = userOpt.get();
        // Kiểm tra role là STAFF (nếu dùng enum thì so sánh với Role.STAFF)
        if (user.getRole() == null || !user.getRole().toString().equalsIgnoreCase("STAFF")) {
            model.addAttribute("error", "Tài khoản không phải là staff!");
            return "staff_login";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu không đúng!");
            return "staff_login";
        }
        // Lưu thông tin đăng nhập vào session
        session.setAttribute("staff", user);
        return "redirect:/staff";
    }

    @GetMapping("")
    public String staffDashboard() {
        return "staff_dashboard";
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println(hashedPassword);
    }
}
//package group6.cinema_project.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/staff")
//public class StaffController {
//    @Autowired
//    private ShowtimeService showtimeService;
//    @Autowired
//    private TicketService ticketService;
//
//    @GetMapping("/showtimes")
//    public String showtimes(Model model) {
//        model.addAttribute("showtimes", showtimeService.findAll());
//        return "staff_showtimes";
//    }
//
//    @GetMapping("/showtimes/{id}/tickets")
//    public String tickets(@PathVariable Long id, Model model) {
//        var showtime = showtimeService.findById(id);
//        var tickets = ticketService.findByShowtimeId(id);
//        model.addAttribute("showtime", showtime);
//        model.addAttribute("tickets", tickets);
//        return "tickets";
//    }
//}
