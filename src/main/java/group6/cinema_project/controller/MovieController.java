package group6.cinema_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import group6.cinema_project.entity.Movie; // Thay thế bằng package entity của bạn
import group6.cinema_project.service.MovieService; // Thay thế bằng package service của bạn

import java.util.List;

@Controller
@RequestMapping("/movies") // Tiền tố chung cho các request liên quan đến Movie
@RequiredArgsConstructor // Lombok inject MovieService
public class MovieController {

    private final MovieService movieService;

    /**
     * Xử lý request để hiển thị trang chủ với danh sách tất cả các bộ phim.
     * @param model Đối tượng Model để truyền dữ liệu tới view.
     * @return Tên của view (Thymeleaf template), trong trường hợp này là "index".
     */
    @GetMapping({"", "/", "/index"}) // Có thể truy cập qua /movies, /movies/, /movies/index
    public String getAllMovies(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies); // Thêm danh sách phim vào model
        return "index"; // Trả về tên file template: index.html
    }

    // Các hàm khác cho CRUD sẽ được thêm vào đây (ví dụ: showMovieById, createMovieForm, ...)
}
