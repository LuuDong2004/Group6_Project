package group6.cinema_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.UserDto;
import group6.cinema_project.entity.Role;
import group6.cinema_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String adminLogin(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("role") String role,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra user có tồn tại và có role phù hợp không
            UserDto user = userService.getUserByEmail(email);
            
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Email không tồn tại!");
                return "redirect:/admin/login";
            }

            // Kiểm tra role
            Role userRole = user.getRole();
            if (role.equals("admin") && userRole != Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập trang Admin!");
                return "redirect:/admin/login";
            }
            
            if (role.equals("staff") && userRole != Role.STAFF) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập trang Staff!");
                return "redirect:/admin/login";
            }

            // Thực hiện authentication
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu context vào session một cách tường minh
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Redirect dựa trên role
            if (role.equals("admin")) {
                return "redirect:/admin";
            } else {
                return "redirect:/staff";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng!");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logout")
    public String adminLogout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "Đăng xuất thành công!");
        return "redirect:/admin/login";
    }
}