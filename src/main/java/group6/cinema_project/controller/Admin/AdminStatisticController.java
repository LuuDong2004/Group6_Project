package group6.cinema_project.controller.Admin;

import group6.cinema_project.service.Admin.IAdminStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class AdminStatisticController {

    @Autowired
    private IAdminStatisticService adminStatisticService;

    @GetMapping("/admin/statistics")
    public String showStatistics(Model model) {
        // Lấy thống kê tổng quan
        Map<String, Object> generalStats = adminStatisticService.getUserMovieStatistics();
        model.addAttribute("generalStats", generalStats);

        // Lấy danh sách thống kê user-movie cho bảng
        List<Map<String, Object>> userMovieStats = adminStatisticService.getUserMovieStatisticsList();
        model.addAttribute("statistics", userMovieStats);

        // Lấy data cho biểu đồ doanh thu phim
        List<String> movieLabels = adminStatisticService.getMovieRevenueLabels();
        List<Double> movieData = adminStatisticService.getMovieRevenueData();
        model.addAttribute("movieLabels", movieLabels);
        model.addAttribute("movieData", movieData);

        return "admin/admin_statistic";
    }
}