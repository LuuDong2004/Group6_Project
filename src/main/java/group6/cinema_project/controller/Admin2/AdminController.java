package group6.cinema_project.controller.Admin2;

import group6.cinema_project.repository.User.UserRepository;
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

    private final UserRepository userRepository;

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
        return "admin2/secret_login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin2/dashboard";
    }

    // // Method để tạo tài khoản staff (chỉ dùng để test)
    // @GetMapping("/create-staff")
    // public String createStaffAccount() {
    // try {
    // // Tạo UserRegistrationDto cho staff
    // UserRegistrationDto staffDto = new UserRegistrationDto();
    // staffDto.setUserName("Staff User");
    // staffDto.setEmail("staff@cinema.com");
    // staffDto.setPassword("123456");
    // staffDto.setConfirmPassword("123456");
    // staffDto.setPhone("0123456789");
    // staffDto.setDateOfBirth(LocalDate.parse("1990-01-01"));
    // staffDto.setAddress("Hà Nội");
    //
    // // Tạo user với role STAFF
    // User user = new User(
    // staffDto.getUserName(),
    // staffDto.getPhone(),
    // staffDto.getEmail(),
    // passwordEncoder.encode(staffDto.getPassword()),
    // staffDto.getDateOfBirth(),
    // staffDto.getAddress(),
    // Role.STAFF
    // );
    //
    // userRepository.save(user);
    // return "redirect:/admin?message=Staff account created successfully";
    // } catch (Exception e) {
    // return "redirect:/admin?error=Failed to create staff account: " +
    // e.getMessage();
    // }
    // }

    // ĐÃ XÓA các method liên quan đến food, đã chuyển sang AdminFoodController

}