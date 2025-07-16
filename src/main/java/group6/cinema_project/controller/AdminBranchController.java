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

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.CinemaChain;
import group6.cinema_project.service.BranchService;
import group6.cinema_project.service.CinemaChainService;

@Controller
@RequestMapping("/admin/branches")
public class AdminBranchController {
    @Autowired
    private BranchService branchService;
    @Autowired
    private CinemaChainService cinemaChainService;

    @GetMapping("")
    public String listBranches(Model model) {
        List<BranchDto> branches = branchService.findAll();
        List<CinemaChain> cinemaChains = cinemaChainService.findAll();
        BranchDto branchDto = new BranchDto();
        branchDto.setCinemaChainId(0);
        model.addAttribute("branches", branches);
        model.addAttribute("cinemaChains", cinemaChains);
        model.addAttribute("branch", branchDto);
        return "admin_branch_management";
    }

    @PostMapping("/add")
    public String addBranch(@ModelAttribute("branch") BranchDto branchDto, RedirectAttributes redirectAttributes) {
        if (branchDto.getCinemaChainId() == 0) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải chọn chuỗi rạp!");
            return "redirect:/admin/branches";
        }
        branchService.save(branchDto);
        redirectAttributes.addFlashAttribute("success", "Thêm chi nhánh thành công!");
        return "redirect:/admin/branches";
    }

    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            branchService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa chi nhánh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa chi nhánh. Chi nhánh có thể đang được sử dụng.");
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/edit/{id}")
    public String editBranchForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        BranchDto branch = branchService.findById(id);
        if (branch == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi nhánh!");
            return "redirect:/admin/branches";
        }
        List<CinemaChain> cinemaChains = cinemaChainService.findAll();
        model.addAttribute("branch", branch);
        model.addAttribute("cinemaChains", cinemaChains);
        return "admin_branch_edit";
    }

    @PostMapping("/edit/{id}")
    public String editBranch(@PathVariable int id, @ModelAttribute("branch") BranchDto branchDto, RedirectAttributes redirectAttributes) {
        branchDto.setId(id);
        branchService.save(branchDto);
        redirectAttributes.addFlashAttribute("success", "Cập nhật chi nhánh thành công!");
        return "redirect:/admin/branches";
    }
} 