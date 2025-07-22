package group6.cinema_project.controller;

import group6.cinema_project.dto.BlogPostDto;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.IBlogService;
import group6.cinema_project.service.IMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller xử lý trang chủ của website.
 * Hiển thị thông tin tổng quan về phim và blog posts.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final IMovieService movieService;
    private final IBlogService blogService;

    /**
     * Hiển thị trang chủ với thông tin phim và blog posts nổi bật.
     */
    @GetMapping("/")
    public String home(Model model) {
        log.info("Hiển thị trang chủ");

        try {
            // Lấy phim nổi bật (top 3 phim có rating cao nhất)
            List<MovieDto> topMovies = movieService.getMoviesByTop3Rating();
            model.addAttribute("topMovies", topMovies);

            // Lấy phim mới nhất (8 phim đầu tiên)
            List<MovieDto> latestMovies = movieService.getMoviesWithPagination(0, 8);
            model.addAttribute("latestMovies", latestMovies);

            // Lấy blog posts nổi bật (3 bài mới nhất)
            List<BlogPostDto> featuredBlogs = blogService.getLatestBlogPosts(3);
            model.addAttribute("featuredBlogs", featuredBlogs);

            // Đếm tổng số blog posts
            Long totalBlogs = blogService.getTotalBlogPostsCount();
            model.addAttribute("totalBlogs", totalBlogs);

            log.info("Đã tải trang chủ với {} phim top, {} phim mới, {} blog posts", 
                    topMovies.size(), latestMovies.size(), featuredBlogs.size());

            return "index";

        } catch (Exception e) {
            log.error("Lỗi khi tải trang chủ", e);
            
            // Fallback: trả về trang chủ với dữ liệu rỗng
            model.addAttribute("topMovies", List.of());
            model.addAttribute("latestMovies", List.of());
            model.addAttribute("featuredBlogs", List.of());
            model.addAttribute("totalBlogs", 0L);
            model.addAttribute("error", "Có lỗi xảy ra khi tải dữ liệu trang chủ");
            
            return "index";
        }
    }

    /**
     * Redirect từ /index.html đến trang chủ.
     */
    @GetMapping("/index.html")
    public String indexHtml() {
        return "redirect:/";
    }

    /**
     * Redirect từ /index đến trang chủ.
     */
    @GetMapping("/index")
    public String index() {
        return "redirect:/";
    }

    /**
     * Hiển thị trang About.
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Hiển thị trang Contact.
     */
    @GetMapping("/contact")
    public String contact() {
        return "Contact_Us";
    }

    /**
     * Xử lý lỗi 404.
     */
    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }

    /**
     * Xử lý lỗi 500.
     */
    @GetMapping("/500")
    public String serverError() {
        return "error/500";
    }
}
