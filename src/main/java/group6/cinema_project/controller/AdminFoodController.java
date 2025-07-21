package group6.cinema_project.controller;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminFoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/admin/foods/view/{id}")
    public String viewFood(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            FoodDto food = foodService.getFoodById(id);
            if (food == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy combo food!");
                return "redirect:/admin/foods/list";
            }
            model.addAttribute("food", food);
            return "admin_food_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/foods/list";
        }
    }
} 