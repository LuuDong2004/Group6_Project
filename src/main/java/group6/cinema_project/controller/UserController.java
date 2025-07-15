package group6.cinema_project.controller;

import group6.cinema_project.dto.UserDto;
import group6.cinema_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        UserDto user = userService.getUserDTOById(id);
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        return "user_profile";
    }


} 