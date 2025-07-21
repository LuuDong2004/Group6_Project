package group6.cinema_project.controller;

import group6.cinema_project.entity.DiscountCode;
import group6.cinema_project.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @GetMapping("/admin")
    public String adminHome() {
        return "admin/admin_actor_list";
    }

    // List vouchers
    @GetMapping("/admin/vouchers")
    public String listVouchers(Model model) {
        List<DiscountCode> vouchers = discountCodeRepository.findAll();
        model.addAttribute("vouchers", vouchers);
        return "admin/admin_voucher_list";
    }

    // Add voucher (GET)
    @GetMapping("/admin/vouchers/add")
    public String addVoucherForm(Model model) {
        model.addAttribute("voucher", new DiscountCode());
        return "admin/admin_voucher_add";
    }

    // Add voucher (POST)
    @PostMapping("/admin/vouchers/add")
    public String addVoucher(@ModelAttribute DiscountCode voucher) {
        discountCodeRepository.save(voucher);
        return "redirect:/admin/vouchers";
    }

    // Edit voucher (GET)
    @GetMapping("/admin/vouchers/edit/{id}")
    public String editVoucher(@PathVariable Long id, Model model) {
        DiscountCode voucher = discountCodeRepository.findById(id).orElseThrow();
        model.addAttribute("voucher", voucher);
        return "admin/admin_voucher_edit";
    }

    // Edit voucher (POST)
    @PostMapping("/admin/vouchers/edit/{id}")
    public String updateVoucher(@PathVariable Long id, @ModelAttribute DiscountCode voucher) {
        voucher.setId(id);
        discountCodeRepository.save(voucher);
        return "redirect:/admin/vouchers";
    }

    // Delete voucher
    @PostMapping("/admin/vouchers/delete/{id}")
    public String deleteVoucher(@PathVariable Long id) {
        discountCodeRepository.deleteById(id);
        return "redirect:/admin/vouchers";
    }
} 