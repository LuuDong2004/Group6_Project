package group6.cinema_project.controller;


import group6.cinema_project.service.impl.StatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

    @Autowired
    private StatisticServiceImpl statisticServiceImpl;

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        model.addAttribute("statistics", statisticServiceImpl.getUserMovieStatistics());
        return "admin/admin_statistic";
    }
}