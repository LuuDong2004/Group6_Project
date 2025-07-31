package group6.cinema_project.controller.Admin2;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.entity.CinemaChain;
import group6.cinema_project.service.Admin.IAdminBranchService;
import group6.cinema_project.service.Admin.IAdminCinemaChainService;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/branches")
public class AdminBranchController {

    @Autowired
    private IAdminBranchService adminBranchService;
    @Autowired
    private IAdminCinemaChainService adminCinemaChainService;

    @GetMapping("")
    public String listBranches(Model model,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                @RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "address", required = false) String address,
                                @RequestParam(value = "cinemaChain", required = false) String cinemaChain) {
        name = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        address = (address != null && !address.trim().isEmpty()) ? address.trim() : null;
        cinemaChain = (cinemaChain != null && !cinemaChain.trim().isEmpty()) ? cinemaChain.trim() : null;
        var branchPage = adminBranchService.getBranchesPage(page, size, name, address, cinemaChain);
        model.addAttribute("branchPage", branchPage);
        model.addAttribute("branches", branchPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", branchPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("cinemaChain", cinemaChain);
        model.addAttribute("branch", new group6.cinema_project.dto.BranchDto());
        model.addAttribute("cinemaChains", adminCinemaChainService.findAll());
        return "admin2/branch_management";
    }

    @PostMapping("/add")
    public String addBranch(@Valid @ModelAttribute("branch") BranchDto branchDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        boolean isDuplicate = adminBranchService.isNameDuplicate(branchDto.getName(), null);
        if (result.hasErrors() || isDuplicate) {
            if (isDuplicate) {
                result.rejectValue("name", "error.branch", "Tên chi nhánh đã tồn tại.");
                model.addAttribute("error", "Tên chi nhánh đã tồn tại!");
            }
            List<CinemaChain> cinemaChains = adminCinemaChainService.findAll();
            Page<BranchDto> branchPage = adminBranchService.getBranchesPage(0, 5);
            model.addAttribute("branchPage", branchPage);
            model.addAttribute("branches", branchPage.getContent());
            model.addAttribute("cinemaChains", cinemaChains);
            model.addAttribute("branch", branchDto);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", branchPage.getTotalPages());
            model.addAttribute("pageSize", 5);
            model.addAttribute("showAddModal", true);
            return "admin2/branch_management";
        }
        if (branchDto.getCinemaChainId() == 0) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải chọn chuỗi rạp!");
            return "redirect:/admin/branches";
        }
        adminBranchService.save(branchDto);
        redirectAttributes.addFlashAttribute("success", "Thêm chi nhánh thành công!");
        return "redirect:/admin/branches";
    }

    @GetMapping("/edit/{id}")
    public String editBranchForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        BranchDto branch = adminBranchService.findById(id);
        if (branch == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi nhánh!");
            return "redirect:/admin/branches";
        }
        List<CinemaChain> cinemaChains = adminCinemaChainService.findAll();
        model.addAttribute("branch", branch);
        model.addAttribute("cinemaChains", cinemaChains);
        return "admin2/branch_edit";
    }

    @PostMapping("/edit/{id}")
    public String editBranch(@PathVariable Integer id, @Valid @ModelAttribute("branch") BranchDto branchDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        boolean isDuplicate = adminBranchService.isNameDuplicate(branchDto.getName(), id);
        if (result.hasErrors() || isDuplicate) {
            if (isDuplicate) {
                result.rejectValue("name", "error.branch", "Tên chi nhánh đã tồn tại.");
                model.addAttribute("error", "Tên chi nhánh đã tồn tại!");
            }
            List<CinemaChain> cinemaChains = adminCinemaChainService.findAll();
            Page<BranchDto> branchPage = adminBranchService.getBranchesPage(0, 5);
            model.addAttribute("branchPage", branchPage);
            model.addAttribute("branches", branchPage.getContent());
            model.addAttribute("cinemaChains", cinemaChains);
            model.addAttribute("branch", branchDto);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", branchPage.getTotalPages());
            model.addAttribute("pageSize", 5);
            return "admin2/branch_management";
        }
        branchDto.setId(id);
        adminBranchService.save(branchDto);
        redirectAttributes.addFlashAttribute("success", "Cập nhật chi nhánh thành công!");
        return "redirect:/admin/branches";
    }
}
