package group6.cinema_project.controller.Admin2;

import java.io.IOException;
import static java.math.BigDecimal.ZERO;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import group6.cinema_project.service.Admin.IAdminFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.FoodDto;

import jakarta.validation.Valid;

@Controller
public class AdminFoodController {
    @Autowired
    private IAdminFoodService adminFoodService;

    private final String FOOD_UPLOAD_DIR = "src/main/resources/static/food/";

    @GetMapping("/admin/foods/list")
    public String listFoods(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "sort", required = false) String sort) {
        search = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<FoodDto> foodPage = adminFoodService.getFoodsPage(page, size, search, sort);
        model.addAttribute("foodPage", foodPage);
        model.addAttribute("foods", foodPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "admin2/food_list";
    }

    @GetMapping("/admin/foods/add")
    public String addFoodForm(Model model) {
        model.addAttribute("food", new FoodDto());
        return "admin2/food_add";
    }

    @PostMapping("/admin/foods/add")
    public String addFood(@Valid @ModelAttribute("food") FoodDto foodDto,
                          BindingResult result,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        boolean isDuplicate = adminFoodService.isNameDuplicate(foodDto.getName(), null);
        // Validate giá và số lượng
        if (foodDto.getPrice() == null || foodDto.getPrice().compareTo(ZERO) <= 0) {
            result.rejectValue("price", "error.food", "Giá phải lớn hơn 0.");
        }
        if (foodDto.getSize() == null || foodDto.getSize() < 0) {
            result.rejectValue("size", "error.food", "Số lượng phải >= 0.");
        }
        if (result.hasErrors() || isDuplicate) {
            if (isDuplicate) {
                result.rejectValue("name", "error.food", "Tên món ăn đã tồn tại.");
                model.addAttribute("error", "Tên món ăn đã tồn tại!");
            }
            model.addAttribute("food", foodDto);
            return "admin2/food_add";
        }
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(FOOD_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                foodDto.setImage("/food/" + fileName);
            }
            adminFoodService.saveOrUpdate(foodDto);
            redirectAttributes.addFlashAttribute("success", "Thêm món ăn thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm món ăn: " + e.getMessage());
        }
        return "redirect:/admin/foods/list";
    }

    @GetMapping("/admin/foods/edit/{id}")
    public String editFoodForm(@PathVariable Integer id, Model model) {
        FoodDto food = adminFoodService.getFoodById(id);
        model.addAttribute("food", food);
        return "admin2/food_edit";
    }

    @PostMapping("/admin/foods/edit/{id}")
    public String editFood(@PathVariable Integer id,
                           @Valid @ModelAttribute("food") FoodDto foodDto,
                           BindingResult result,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        boolean isDuplicate = adminFoodService.isNameDuplicate(foodDto.getName(), id);
        // Validate giá và số lượng
        if (foodDto.getPrice() == null || foodDto.getPrice().compareTo(ZERO) <= 0) {
            result.rejectValue("price", "error.food", "Giá phải lớn hơn 0.");
        }
        if (foodDto.getSize() == null || foodDto.getSize() < 0) {
            result.rejectValue("size", "error.food", "Số lượng phải >= 0.");
        }
        if (result.hasErrors() || isDuplicate) {
            if (isDuplicate) {
                result.rejectValue("name", "error.food", "Tên món ăn đã tồn tại.");
                model.addAttribute("error", "Tên món ăn đã tồn tại!");
            }
            model.addAttribute("food", foodDto);
            return "admin2/food_edit";
        }
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
                FoodDto old = adminFoodService.getFoodById(id);
                foodDto.setImage(old.getImage());
            }
            foodDto.setId(id);
            adminFoodService.saveOrUpdate(foodDto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật món ăn thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật món ăn: " + e.getMessage());
        }
        return "redirect:/admin/foods/list";
    }

    @GetMapping("/admin/foods/delete/{id}")
    public String deleteFood(@PathVariable Integer id) {
        adminFoodService.deleteFood(id);
        return "redirect:/admin/foods/list";
    }

    @GetMapping("/admin/foods/view/{id}")
    public String viewFood(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            FoodDto food = adminFoodService.getFoodById(id);
            if (food == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy combo food!");
                return "redirect:/admin/foods/list";
            }
            model.addAttribute("food", food);
            return "admin2/food_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/foods/list";
        }
    }
}