package group6.cinema_project.controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String errorPage(@RequestParam(required = false) String message,
                           @RequestParam(required = false) Integer movieId,
                           Model model) {
        
        // Clean the error message to prevent any issues
        if (message != null) {
            message = message.replaceAll("[\r\n]", " ").trim();
        }
        
        model.addAttribute("error", message);
        model.addAttribute("movieId", movieId);
        model.addAttribute("timestamp", System.currentTimeMillis());
        
        return "error";
    }
}
