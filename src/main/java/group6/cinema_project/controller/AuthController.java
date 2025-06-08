package group6.cinema_project.controller;

import group6.cinema_project.dto.ChangePasswordDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.dto.UserLoginDto;
import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }
        model.addAttribute("loginUser", new UserLoginDto());

        model.addAttribute("user", new UserRegistrationDto());



        return "sign_in";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "sign_in";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (result.hasErrors()) {
            return "sign_in";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "sign_in";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        try {
            // Lấy email của user hiện tại từ SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UserDto user = userService.getUserByEmail(email);
            model.addAttribute("user", user);

            return "userDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin người dùng");
            return "redirect:/login";
        }
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy email của user hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UserDto updatedUser = userService.updateUserProfile(email, userDto);

            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy email của user hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            boolean success = userService.changePassword(email, changePasswordDto);

            if (success) {
                response.put("success", true);
                response.put("message", "Đổi mật khẩu thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Đổi mật khẩu thất bại!");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/current")
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            UserDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }




    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
}
