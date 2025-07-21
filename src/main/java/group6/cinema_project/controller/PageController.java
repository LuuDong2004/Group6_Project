package group6.cinema_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/movie_theater")
    public String movieTheaterPage() {
        return "movie_theate";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @GetMapping("/seat-selection")
    public String seatSelectionPage() {
        return "seat_selection/seat_sel";
    }

    @GetMapping("/person_detail.html")
    public String personDetailPage() {
        return "person_detail";
    }

    @GetMapping("/payment")
    public String paymentPage(Model model) {
        // Demo booking data
        Map<String, Object> movie = new HashMap<>();
        movie.put("name", "Commando 3");
        movie.put("image", "/web/assets/images/vidyut.jpg");
        Map<String, Object> branch = new HashMap<>();
        branch.put("name", "CGV Vincom Center");
        Map<String, Object> screeningRoom = new HashMap<>();
        screeningRoom.put("name", "Room 2");
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("movie", movie);
        schedule.put("screeningDate", LocalDate.now());
        schedule.put("startTime", LocalTime.of(19, 30));
        schedule.put("branch", branch);
        schedule.put("screeningRoom", screeningRoom);
        Map<String, Object> booking = new HashMap<>();
        booking.put("schedule", schedule);
        booking.put("seatNames", Arrays.asList("A1", "A2", "A3"));
        booking.put("amount", 300000);
        booking.put("id", 12345);
        model.addAttribute("booking", booking);
        // Demo: mã giảm giá
        model.addAttribute("discountCodes", Arrays.asList("GIAM10", "GIAM20"));
        return "payment";
    }
} 