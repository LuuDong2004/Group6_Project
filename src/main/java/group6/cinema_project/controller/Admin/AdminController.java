package group6.cinema_project.controller.Admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    //private final UserRepository userRepository;

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

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/admin_dashboard";
    }


}