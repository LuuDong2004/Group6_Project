package group6.cinema_project.controller.Admin2;


import group6.cinema_project.service.Admin.impl.AdminStatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminStatisticController {

    @Autowired
    private AdminStatisticServiceImpl adminStatisticService;

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        model.addAttribute("statistics", adminStatisticService.getUserMovieStatistics());
        return "admin/admin_statistic";
    }
}