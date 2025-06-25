package group6.cinema_project.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.ChangePasswordDto;
import group6.cinema_project.dto.PasswordResetConfirmDto;
import group6.cinema_project.dto.PasswordResetRequestDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.dto.UserLoginDto;
import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "continue", required = false) String continueUrl,
                            Model model,
                            HttpServletRequest request) {


        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }
        //luu thong tin url neu co
        if (continueUrl != null && !continueUrl.trim().isEmpty()) {
            request.getSession().setAttribute("continueAfterLogin", continueUrl);
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

    // Password Reset Endpoints
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("resetRequest", new PasswordResetRequestDto());
        return "forgot_password";
    }
    
    @PostMapping("/forgot-password")
    public String requestPasswordReset(@Valid @ModelAttribute("resetRequest") PasswordResetRequestDto requestDto,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "forgot_password";
        }
        
        try {
            userService.requestPasswordReset(requestDto);
            redirectAttributes.addFlashAttribute("success", 
                "Nếu email tồn tại trong hệ thống, chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "forgot_password";
        }
    }
    
    @GetMapping("/reset-password/confirm")
    public String resetPasswordConfirmPage(@RequestParam("token") String token, Model model) {
        if (!userService.validateResetToken(token)) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            return "error";
        }
        
        PasswordResetConfirmDto confirmDto = new PasswordResetConfirmDto();
        confirmDto.setToken(token);
        model.addAttribute("resetConfirm", confirmDto);
        return "reset_password";
    }
    
    @PostMapping("/reset-password/confirm")
    public String confirmPasswordReset(@Valid @ModelAttribute("resetConfirm") PasswordResetConfirmDto confirmDto,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reset_password";
        }
        
        try {
            userService.confirmPasswordReset(confirmDto);
            redirectAttributes.addFlashAttribute("success", "Mật khẩu đã được đặt lại thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset_password";
        }
    }
    
    // API endpoints for AJAX calls
    @PostMapping("/api/forgot-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> requestPasswordResetApi(@RequestBody PasswordResetRequestDto requestDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            userService.requestPasswordReset(requestDto);
            response.put("success", true);
            response.put("message", "Nếu email tồn tại trong hệ thống, chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/reset-password/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmPasswordResetApi(@RequestBody PasswordResetConfirmDto confirmDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            userService.confirmPasswordReset(confirmDto);
            response.put("success", true);
            response.put("message", "Mật khẩu đã được đặt lại thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
}
