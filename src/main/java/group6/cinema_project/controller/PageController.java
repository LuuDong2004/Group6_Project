package group6.cinema_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
} 