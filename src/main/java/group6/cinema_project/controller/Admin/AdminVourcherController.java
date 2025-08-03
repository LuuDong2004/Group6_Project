package group6.cinema_project.controller.Admin2;

import group6.cinema_project.entity.Qa.Voucher;
import group6.cinema_project.repository.User.VoucherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminVourcherController {

    @Autowired
    private VoucherRepository discountCodeRepository;
//
//    @GetMapping("/admin")
//    public String adminHome() {
//        return "admin/admin_actor_list";
//    }

    // List vouchers
    @GetMapping("/admin/vouchers")
    public String listVouchers(Model model) {
        List<Voucher> vouchers = discountCodeRepository.findAll();
        model.addAttribute("vouchers", vouchers);
        return "admin/admin_voucher_list";
    }

    // Add voucher (GET)
    @GetMapping("/admin/vouchers/add")
    public String addVoucherForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "admin/admin_voucher_add";
    }

    // Add voucher (POST)
    @PostMapping("/admin/vouchers/add")
    public String addVoucher(@ModelAttribute Voucher voucher) {
        discountCodeRepository.save(voucher);
        return "redirect:/admin/vouchers";
    }

    // Edit voucher (GET)
    @GetMapping("/admin/vouchers/edit/{id}")
    public String editVoucher(@PathVariable Long id, Model model) {
        Voucher voucher = discountCodeRepository.findById(id).orElseThrow();
        model.addAttribute("voucher", voucher);
        return "admin/admin_voucher_edit";
    }

    // Edit voucher (POST)
    @PostMapping("/admin/vouchers/edit/{id}")
    public String updateVoucher(@PathVariable Long id, @ModelAttribute Voucher voucher) {
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
