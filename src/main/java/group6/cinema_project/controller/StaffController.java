package group6.cinema_project.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    @GetMapping("/login")
    public String staffLoginPage() {
        return "redirect:/admin/login";

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
