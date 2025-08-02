package group6.cinema_project.controller.User;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.User.IHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private IHomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        List<MovieDto> popularMovies = homeService.getPopularMovies();
        List<MovieDto> newReleases = homeService.getNewReleases();
        model.addAttribute("popularMovies", popularMovies);
        model.addAttribute("newReleases", newReleases);
        // Thêm timestamp để tránh cache hình ảnh
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "index";
    }
}