package group6.cinema_project.controller.Admin2;

import java.util.HashMap;
import java.util.Map;

import group6.cinema_project.dto.Login.UserRegistrationDto;
import group6.cinema_project.service.User.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.AdminPasswordResetDto;
import group6.cinema_project.dto.UserDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    @Autowired
    private IUserService userService;

    // Danh sách user
    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin2/user_list";
    }

    // Hiển thị form thêm user
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "admin2/user_add";
    }

    // Xử lý thêm user
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin2/user_add";
        }
        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/add";
        }
    }

    // Xem chi tiết user
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UserDto user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "admin2/user_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/users/list";
        }
    }

    // Xóa user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng!");
        }
        return "redirect:/admin/users/list";
    }

    // Reset mật khẩu user
    @GetMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            String newPassword = userService.resetPassword(id);
            UserDto user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("resetPasswordSuccess", true);
            model.addAttribute("newPassword", newPassword);
            return "admin2/user_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể reset mật khẩu: " + e.getMessage());
            return "redirect:/admin/users/list";
        }
    }

    // Reset mật khẩu user với tùy chọn
    @PostMapping("/reset-password/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPasswordWithOptions(@PathVariable int id,
                                                                        @RequestBody AdminPasswordResetDto adminPasswordResetDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            adminPasswordResetDto.setUserId(id);
            boolean success = userService.adminResetPassword(adminPasswordResetDto);

            if (success) {
                UserDto user = userService.getUserById(id);
                response.put("success", true);
                response.put("message", "Reset mật khẩu thành công!");
                response.put("user", user);

                // Trả về mật khẩu mới nếu không gửi email
                if (!adminPasswordResetDto.isSendEmail()) {
                    response.put("newPassword", adminPasswordResetDto.hasCustomPassword() ?
                            adminPasswordResetDto.getCustomPassword() : "Mật khẩu ngẫu nhiên đã được tạo");
                }
            } else {
                response.put("success", false);
                response.put("message", "Reset mật khẩu thất bại!");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // API endpoint để reset password
    @PostMapping("/api/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiResetPassword(@RequestBody AdminPasswordResetDto adminPasswordResetDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = userService.adminResetPassword(adminPasswordResetDto);

            if (success) {
                UserDto user = userService.getUserById(adminPasswordResetDto.getUserId());
                response.put("success", true);
                response.put("message", "Reset mật khẩu thành công!");
                response.put("user", user);

                // Trả về mật khẩu mới nếu không gửi email
                if (!adminPasswordResetDto.isSendEmail()) {
                    response.put("newPassword", adminPasswordResetDto.hasCustomPassword() ?
                            adminPasswordResetDto.getCustomPassword() : "Mật khẩu ngẫu nhiên đã được tạo");
                }
            } else {
                response.put("success", false);
                response.put("message", "Reset mật khẩu thất bại!");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
