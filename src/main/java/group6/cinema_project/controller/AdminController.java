package group6.cinema_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.entity.Role;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.ActorService;
import group6.cinema_project.service.BranchService;
import group6.cinema_project.service.DirectorService;
import group6.cinema_project.service.FoodService;
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
    private final FoodService foodService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BranchService branchService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/movies/";
    private final String FOOD_UPLOAD_DIR = "src/main/resources/static/food/";

    @GetMapping("/secret-login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {

        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }

        return "admin_secret_login";
    }

   

    @GetMapping
    public String adminDashboard() {
        return "admin_dashboard";
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

    

    @GetMapping("/foods/list")
    public String listFoods(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<FoodDto> foodPage = foodService.getFoodsPage(page, size);
        model.addAttribute("foodPage", foodPage);
        model.addAttribute("foods", foodPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodPage.getTotalPages());
        model.addAttribute("pageSize", size);
        return "admin_food_list";
    }
    @GetMapping("/foods/add")
    public String addFoodForm(Model model) {
        model.addAttribute("food", new FoodDto());
        return "admin_food_add";
    }

    // Handle add
    @PostMapping("/foods/add")
    public String addFood(@ModelAttribute FoodDto foodDto, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(FOOD_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                foodDto.setImage("/food/" + fileName);
            }
            foodService.saveOrUpdate(foodDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/foods/list";
    }
    // Show edit form
    @GetMapping("/foods/edit/{id}")
    public String editFoodForm(@PathVariable Integer id, Model model) {
        FoodDto food = foodService.getFoodById(id);
        model.addAttribute("food", food);
        return "admin_food_edit";
    }

    // Handle edit
    @PostMapping("/foods/edit/{id}")
    public String editFood(@PathVariable Integer id, @ModelAttribute FoodDto foodDto, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(FOOD_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                foodDto.setImage("/food/" + fileName);
            } else {
                // giữ lại ảnh cũ nếu không upload mới
                FoodDto old = foodService.getFoodById(id);
                foodDto.setImage(old.getImage());
            }
            foodDto.setId(id);
            foodService.saveOrUpdate(foodDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/foods/list";
    }

    // Delete
    @GetMapping("/foods/delete/{id}")
    public String deleteFood(@PathVariable Integer id) {
        foodService.deleteFood(id);
        return "redirect:/admin/foods/list";
    }
    
//    // --- Branch Management ---
//    @GetMapping("/branches/list")
//    public String listBranches(Model model) {
//        model.addAttribute("branches", branchService.findAll());
//        model.addAttribute("branch", new Branch());
//        return "admin_branch_management";
//    }
//
//    @PostMapping("/branches/add")
//    public String addBranch(@ModelAttribute BranchDto branchDto, RedirectAttributes redirectAttributes) {
//        branchService.save(branchDto);
//        redirectAttributes.addFlashAttribute("success", "Thêm chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
//    @GetMapping("/branches/edit/{id}")
//    public String editBranchForm(@PathVariable Integer id, Model model) {
//        Branch branch = branchService.findById(id);
//        model.addAttribute("branch", branch);
//        return "admin_branch_edit";
//    }
//
//    @PostMapping("/branches/edit/{id}")
//    public String editBranch(@PathVariable Integer id, @ModelAttribute Branch branch, RedirectAttributes redirectAttributes) {
//        branch.setId(id);
//        branchService.saveOrUpdate(branch);
//        redirectAttributes.addFlashAttribute("success", "Cập nhật chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
//    @GetMapping("/branches/delete/{id}")
//    public String deleteBranch(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
//        branchService.deleteBranch(id);
//        redirectAttributes.addFlashAttribute("success", "Xóa chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
}
