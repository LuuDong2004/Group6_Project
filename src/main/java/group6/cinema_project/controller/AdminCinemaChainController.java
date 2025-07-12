package group6.cinema_project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.CinemaChainDto;
import group6.cinema_project.entity.CinemaChain;
import group6.cinema_project.service.CinemaChainService;

@Controller
@RequestMapping("/admin/cinema-chains")
public class AdminCinemaChainController {
    @Autowired
    private CinemaChainService cinemaChainService;

    @GetMapping("")
    public String listCinemaChains(Model model) {
        List<CinemaChainDto> chains = cinemaChainService.findAll().stream()
                .map(CinemaChainDto::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("cinemaChains", chains);
        model.addAttribute("cinemaChain", new CinemaChainDto());
        return "admin_cinema_chain_management";
    }

    @PostMapping("/add")
    public String addCinemaChain(@ModelAttribute("cinemaChain") CinemaChainDto cinemaChainDto, RedirectAttributes redirectAttributes) {
        try {
            cinemaChainService.save(cinemaChainDto.toEntity());
            redirectAttributes.addFlashAttribute("success", "Thêm chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm chuỗi rạp: " + e.getMessage());
        }
        return "redirect:/admin/cinema-chains";
    }

    @GetMapping("/edit/{id}")
    public String editCinemaChainForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CinemaChain chain = cinemaChainService.findById(id);
            if (chain == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuỗi rạp!");
                return "redirect:/admin/cinema-chains";
            }
            model.addAttribute("cinemaChain", CinemaChainDto.fromEntity(chain));
            return "admin_cinema_chain_edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/cinema-chains";
        }
    }

    @PostMapping("/edit/{id}")
    public String editCinemaChain(@PathVariable int id, @ModelAttribute("cinemaChain") CinemaChainDto cinemaChainDto, RedirectAttributes redirectAttributes) {
        try {
            CinemaChain entity = cinemaChainDto.toEntity();
            entity.setId(id);
            cinemaChainService.save(entity);
            redirectAttributes.addFlashAttribute("success", "Cập nhật chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật chuỗi rạp: " + e.getMessage());
        }
        return "redirect:/admin/cinema-chains";
    }

    @GetMapping("/delete/{id}")
    public String deleteCinemaChain(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            cinemaChainService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa chuỗi rạp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa chuỗi rạp. Chuỗi rạp có thể đang được sử dụng.");
        }
        return "redirect:/admin/cinema-chains";
    }
} 