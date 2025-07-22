package group6.cinema_project.controller.admin;

import java.time.LocalDate;

import group6.cinema_project.service.admin.AdminBranchService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.entity.Role;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.ActorService;
import group6.cinema_project.service.DirectorService;
import group6.cinema_project.service.UserService;
import group6.cinema_project.service.impl.MovieServiceImpl;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MovieServiceImpl movieService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminBranchService adminBranchService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/movies/";

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
        return "admin/secret_login";
    }

   

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    // Method để tạo tài khoản staff (chỉ dùng để test)
    @GetMapping("/create-staff")
    public String createStaffAccount() {
        try {
            // Tạo UserRegistrationDto cho staff
            UserRegistrationDto staffDto = new UserRegistrationDto();
            staffDto.setUserName("Staff User");
            staffDto.setEmail("staff@cinema.com");
            staffDto.setPassword("123456");
            staffDto.setConfirmPassword("123456");
            staffDto.setPhone("0123456789");
            staffDto.setDateOfBirth(LocalDate.parse("1990-01-01"));
            staffDto.setAddress("Hà Nội");
            
            // Tạo user với role STAFF
            User user = new User(
                staffDto.getUserName(),
                staffDto.getPhone(),
                staffDto.getEmail(),
                passwordEncoder.encode(staffDto.getPassword()),
                staffDto.getDateOfBirth(),
                staffDto.getAddress(),
                Role.STAFF
            );
            
            userRepository.save(user);
            return "redirect:/admin?message=Staff account created successfully";
        } catch (Exception e) {
            return "redirect:/admin?error=Failed to create staff account: " + e.getMessage();
        }
    }

    

    // ĐÃ XÓA các method liên quan đến food, đã chuyển sang AdminFoodController
    
}
