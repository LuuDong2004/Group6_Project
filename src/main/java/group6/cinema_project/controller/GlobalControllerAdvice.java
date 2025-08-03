package group6.cinema_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import group6.cinema_project.dto.UserDto;
import group6.cinema_project.service.User.IUserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private IUserService userService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String email = auth.getName();
                UserDto currentUser = userService.getUserByEmail(email);
                model.addAttribute("currentUser", currentUser);
            }
        } catch (Exception e) {
            // Log error but don't break the page
            System.err.println("Error getting current user: " + e.getMessage());
        }
    }
}
