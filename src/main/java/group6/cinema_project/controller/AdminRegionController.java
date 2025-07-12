package group6.cinema_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.RegionDto;
import group6.cinema_project.service.RegionService;

@Controller
@RequestMapping("/admin/regions")
public class AdminRegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("")
    public String listRegions(Model model) {
        List<RegionDto> regions = regionService.findAll();
        model.addAttribute("regions", regions);
        model.addAttribute("region", new RegionDto());
        return "admin_region_management";
    }

    @PostMapping("/add")
    public String addRegion(@ModelAttribute("region") RegionDto regionDto, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra trùng lặp trước khi thêm
            if (regionService.isDuplicateRegion(regionDto.getName(), regionDto.getType())) {
                redirectAttributes.addFlashAttribute("error", 
                    "Khu vực '" + regionDto.getName() + "' trong miền '" + regionDto.getType() + "' đã tồn tại!");
                return "redirect:/admin/regions";
            }
            
            regionService.save(regionDto);
            redirectAttributes.addFlashAttribute("success", "Thêm khu vực thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm khu vực: " + e.getMessage());
        }
        return "redirect:/admin/regions";
    }

    @GetMapping("/edit/{id}")
    public String editRegionForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            RegionDto region = regionService.findById(id);
            if (region == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khu vực!");
                return "redirect:/admin/regions";
            }
            model.addAttribute("region", region);
            return "admin_region_edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/regions";
        }
    }

    @PostMapping("/edit/{id}")
    public String editRegion(@PathVariable int id, @ModelAttribute("region") RegionDto regionDto, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra trùng lặp trước khi cập nhật (trừ ID hiện tại)
            if (regionService.isDuplicateRegionForUpdate(regionDto.getName(), regionDto.getType(), id)) {
                redirectAttributes.addFlashAttribute("error", 
                    "Khu vực '" + regionDto.getName() + "' trong miền '" + regionDto.getType() + "' đã tồn tại!");
                return "redirect:/admin/regions/edit/" + id;
            }
            
            regionDto.setId(id);
            regionService.save(regionDto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khu vực thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật khu vực: " + e.getMessage());
        }
        return "redirect:/admin/regions";
    }

    @GetMapping("/delete/{id}")
    public String deleteRegion(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            regionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khu vực thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa khu vực. Khu vực có thể đang được sử dụng.");
        }
        return "redirect:/admin/regions";
    }
} 