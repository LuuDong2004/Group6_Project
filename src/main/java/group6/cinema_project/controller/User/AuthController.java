package group6.cinema_project.controller.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.dto.Login.ChangePasswordDto;
import group6.cinema_project.dto.Login.PasswordResetConfirmDto;
import group6.cinema_project.dto.Login.PasswordResetRequestDto;
import group6.cinema_project.dto.Login.UserLoginDto;
import group6.cinema_project.dto.Login.UserRegistrationDto;
import group6.cinema_project.dto.UserDto;
import group6.cinema_project.service.User.IBookingService;
import group6.cinema_project.service.User.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "continue", required = false) String continueUrl,
            Model model,
            HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Nếu đã đăng nhập, redirect về trang chủ
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }
        // luu thong tin url neu co
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

        // Validation bổ sung cho password matching
        if (!registrationDto.isPasswordsMatching()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp!");
        }

        // Kiểm tra validation errors
        if (result.hasErrors()) {
            model.addAttribute("loginUser", new UserLoginDto());
            return "sign_in";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginUser", new UserLoginDto());
            return "sign_in";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        try {
            // Lấy email của user hiện tại từ SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // Debug logging
            System.out.println("Current authentication: " + auth);
            System.out.println("Current user email: " + email);
            System.out.println("Authentication type: " + auth.getClass().getSimpleName());

            UserDto user = userService.getUserByEmail(email);
            model.addAttribute("user", user);

            // Lấy booking PAID trong 3 tháng gần nhất, đã sắp xếp theo ngày suất chiếu mới
            // nhất
            List<BookingDto> allPaidBookings = new ArrayList<>();
            try {
                if (user != null) {
                    java.time.LocalDate threeMonthsAgo = java.time.LocalDate.now().minusMonths(3);
                    allPaidBookings = bookingService
                            .getPaidBookingsByUserIdAndDateAfterSortedByShowDateDesc(user.getId(), threeMonthsAgo);
                }
            } catch (Exception ex) {
                System.err.println("Không thể lấy lịch sử đặt vé: " + ex.getMessage());
            }

            int pageSize = 5;
            int total = allPaidBookings.size();
            int totalPages = (int) Math.ceil((double) total / pageSize);
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);
            java.util.List<group6.cinema_project.dto.BookingDto> bookings = (fromIndex < total)
                    ? allPaidBookings.subList(fromIndex, toIndex)
                    : java.util.Collections.emptyList();

            // Tính toán trạng thái hết hạn cho mỗi booking và tạo map
            java.time.LocalDate currentDate = java.time.LocalDate.now();
            java.util.Map<Integer, Boolean> expiredMap = new java.util.HashMap<>();
            for (group6.cinema_project.dto.BookingDto booking : bookings) {
                boolean isExpired = false;
                if (booking.getSchedule() != null && booking.getSchedule().getScreeningDate() != null) {
                    isExpired = booking.getSchedule().getScreeningDate().isBefore(currentDate);
                }
                expiredMap.put(booking.getId(), isExpired);
            }

            model.addAttribute("bookings", bookings);
            model.addAttribute("expiredMap", expiredMap);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);

            // Add CSRF token info for debugging
            if (auth.getPrincipal() instanceof group6.cinema_project.security.oauth2.CustomOAuth2User) {
                System.out.println("OAuth2 user detected in profile page");
                model.addAttribute("isOAuth2User", true);
            } else {
                System.out.println("Local user detected in profile page");
                model.addAttribute("isOAuth2User", false);
            }

            return "userDetail";

        } catch (Exception e) {
            System.err.println("Error in profilePage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải thông tin người dùng: " + e.getMessage());
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

            System.out.println("AuthController.updateProfile - Updating profile for email: " + email);
            System.out.println("AuthController.updateProfile - Request data: " + userDto);

            UserDto updatedUser = userService.updateUserProfile(email, userDto);

            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("user", updatedUser);

            System.out.println("AuthController.updateProfile - Update successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("AuthController.updateProfile - Error: " + e.getMessage());
            e.printStackTrace();
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
    public ResponseEntity<Map<String, Object>> requestPasswordResetApi(
            @RequestBody PasswordResetRequestDto requestDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            userService.requestPasswordReset(requestDto);
            response.put("success", true);
            response.put("message",
                    "Nếu email tồn tại trong hệ thống, chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/reset-password/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmPasswordResetApi(
            @RequestBody PasswordResetConfirmDto confirmDto) {
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("AuthController.logout - Logging out user");

            // Get current authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                System.out.println("AuthController.logout - Logging out user: " + auth.getName());
                System.out.println("AuthController.logout - Authentication type: " + auth.getClass().getSimpleName());

                // Check if it's OAuth2 user
                if (auth.getPrincipal() instanceof group6.cinema_project.security.oauth2.CustomOAuth2User) {
                    System.out.println("AuthController.logout - OAuth2 user detected");
                    group6.cinema_project.security.oauth2.CustomOAuth2User oauth2User = (group6.cinema_project.security.oauth2.CustomOAuth2User) auth
                            .getPrincipal();
                    System.out
                            .println("AuthController.logout - OAuth2 provider: " + oauth2User.getUser().getProvider());
                }
            }

            // Clear the security context
            SecurityContextHolder.clearContext();

            // Invalidate the session
            HttpSession session = request.getSession(false);
            if (session != null) {
                System.out.println("AuthController.logout - Invalidating session");
                session.invalidate();
            }

            // Clear any cookies
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                    }
                }
            }

            redirectAttributes.addFlashAttribute("message", "Đăng xuất thành công!");
            System.out.println("AuthController.logout - Logout successful");

            return "redirect:/login?logout=true";
        } catch (Exception e) {
            System.err.println("AuthController.logout - Error during logout: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng xuất!");
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
}