package group6.cinema_project.controller.Admin2;

import group6.cinema_project.dto.CinemaChainDto;
import group6.cinema_project.service.Admin.IAdminCinemaChainService;
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


import group6.cinema_project.entity.CinemaChain;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/cinema-chains")
public class AdminCinemaChainController {
    @Autowired
    private IAdminCinemaChainService adminCinemaChainService;

    @GetMapping("")
    public String listCinemaChains(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                   @RequestParam(value = "search", required = false) String search) {
        search = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<CinemaChain> chainPage = adminCinemaChainService.getCinemaChainsPage(page, size, search);
        model.addAttribute("chainPage", chainPage);
        model.addAttribute("cinemaChains", chainPage.getContent());
        model.addAttribute("cinemaChain", new CinemaChainDto());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", chainPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        return "admin2/cinema_chain_management";
    }

    @PostMapping("/add")
    public String addCinemaChain(@Valid @ModelAttribute("cinemaChain") CinemaChainDto cinemaChainDto, BindingResult result, Model model ,RedirectAttributes redirectAttributes) {
        boolean isDuplicate = adminCinemaChainService.isNameDuplicate(cinemaChainDto.getName(), null);
        if (result.hasErrors() || isDuplicate) {
            if (isDuplicate) {

                model.addAttribute("error", "Tên chuỗi rạp đã tồn tại!");
            }
            // Lấy lại danh sách để render lại trang
            Page<CinemaChain> chainPage = adminCinemaChainService.getCinemaChainsPage(0, 5, null);
            model.addAttribute("chainPage", chainPage);
            model.addAttribute("cinemaChains", chainPage.getContent());
            model.addAttribute("cinemaChain", cinemaChainDto);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", chainPage.getTotalPages());
            model.addAttribute("pageSize", 5);
            model.addAttribute("showAddModal", true); // Để mở lại modal khi có lỗi
            return "admin2/cinema_chain_management";
        }
        try {
            adminCinemaChainService.save(cinemaChainDto.toEntity());
            redirectAttributes.addFlashAttribute("success", "Thêm chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm chuỗi rạp: " + e.getMessage());
        }
        return "redirect:/admin/cinema-chains";
    }

    @GetMapping("/edit/{id}")
    public String editCinemaChainForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CinemaChain chain = adminCinemaChainService.findById(id);
            if (chain == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuỗi rạp!");
                return "redirect:/admin/cinema-chains";
            }
            model.addAttribute("cinemaChain", CinemaChainDto.fromEntity(chain));
            return "admin2/cinema_chain_edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/cinema-chains";
        }
    }

    @PostMapping("/edit/{id}")
    public String editCinemaChain(@PathVariable Integer id, @Valid @ModelAttribute("cinemaChain") CinemaChainDto cinemaChainDto, BindingResult result, Model model,RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("cinemaChain", cinemaChainDto);
            return "admin2/cinema_chain_edit";
        }
        if (adminCinemaChainService.isNameDuplicate(cinemaChainDto.getName(), id)) {
            result.rejectValue("name", "error.cinemaChain", "Tên chuỗi rạp đã tồn tại.");
            model.addAttribute("cinemaChain", cinemaChainDto);
            return "admin2/cinema_chain_edit";
        }
        try {
            CinemaChain entity = cinemaChainDto.toEntity();
            entity.setId(id);
            adminCinemaChainService.save(entity);
            redirectAttributes.addFlashAttribute("success", "Cập nhật chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật chuỗi rạp: " + e.getMessage());
        }
        return "redirect:/admin/cinema-chains";
    }

    @GetMapping("/delete/{id}")
    public String deleteCinemaChain(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            adminCinemaChainService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa chuỗi rạp. Chuỗi rạp có thể đang được sử dụng.");
        }
        return "redirect:/admin/cinema-chains";
    }

    @GetMapping("/view/{id}")
    public String viewCinemaChain(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CinemaChain chain = adminCinemaChainService.findById(id);
            if (chain == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuỗi rạp!");
                return "redirect:/admin/cinema-chains";
            }
            model.addAttribute("cinemaChain", CinemaChainDto.fromEntity(chain));
            model.addAttribute("branches", chain.getBranches());
            return "admin2/cinema_chain_view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/cinema-chains";
        }
    }
}
