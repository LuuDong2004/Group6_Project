package group6.cinema_project.controller.Admin;

import group6.cinema_project.service.Admin.IAdminActorService;
import group6.cinema_project.service.Admin.IAdminDirectorService;
import group6.cinema_project.service.Admin.IAdminMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {
    private final IAdminMovieService movieService;
    private final IAdminActorService actorService;
    private final IAdminDirectorService directorService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/movies/";

    @PostConstruct
    public void init() {
        try {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public String listMovies(Model model,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "filterBy", required = false, defaultValue = "name") String filterBy,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        // Tạo Pageable object với page và size
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDto> moviePage;

        // Sử dụng các phương thức pagination mới
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            moviePage = movieService.getFilteredMoviesForDisplayWithPagination(searchTerm, filterBy, pageable);
        } else {
            moviePage = movieService.getAllMoviesForDisplayWithPagination(pageable);
        }

        // Thêm thông tin pagination vào model
        model.addAttribute("moviePage", moviePage);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("totalElements", moviePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        model.addAttribute("filterBy", filterBy);

        // Tính toán các trang hiển thị trong pagination
        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(moviePage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/admin_movie_list";
    }

    @GetMapping("/add")
    public String adminMovieAdd(Model model) {
        // Add all directors and actors to the model for autocomplete functionality
        model.addAttribute("allDirectors", directorService.getAllDirectors());
        model.addAttribute("allActors", actorService.getAllActors());
        return "admin/admin_movie_add";
    }

    @PostMapping("/add")
    public String adminMovieAddPost(Model model,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("duration") int duration,
            @RequestParam("rating") String rating,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "genres", required = false) List<String> genres,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "selectedDirectors", required = false) List<Integer> selectedDirectorIds,
            @RequestParam(value = "selectedActors", required = false) List<Integer> selectedActorIds) {

        try {
            MovieDto movie = new MovieDto();
            movie.setName(name);
            movie.setDescription(description);
            movie.setDuration(duration);
            movie.setRating(rating);
            movie.setLanguage("Vietnamese"); // Set default language

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse(releaseDate);
            Date sqlReleaseDate = new Date(utilDate.getTime());
            movie.setReleaseDate(sqlReleaseDate);

            if (genres != null && !genres.isEmpty()) {
                String genreString = String.join(", ", genres);
                movie.setGenre(genreString);
            }

            if (!image.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                movie.setImage("/uploads/movies/" + fileName);
            }

            if (trailerUrl != null && !trailerUrl.trim().isEmpty()) {
                movie.setTrailer(trailerUrl);
            }
            // Handle selected directors
            if (selectedDirectorIds != null && !selectedDirectorIds.isEmpty()) {
                Set<String> directorNames = selectedDirectorIds.stream()
                        .map(id -> directorService.getDirectorById(id))
                        .filter(director -> director != null)
                        .map(Director::getName)
                        .collect(Collectors.toSet());
                movie.setDirectors(directorNames);
            }

            // Handle selected actors
            if (selectedActorIds != null && !selectedActorIds.isEmpty()) {
                Set<String> actorNames = selectedActorIds.stream()
                        .map(id -> actorService.getActorById(id))
                        .filter(actor -> actor != null)
                        .map(Actor::getName)
                        .collect(Collectors.toSet());
                movie.setActors(actorNames);
            }

            movieService.saveOrUpdate(movie);

            System.out.println("Movie saved successfully: " + movie.getName());

        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error saving image file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error saving movie: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/movies/list";
    }

    @GetMapping("/edit/{id}")
    public String editMovie(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<MovieDto> movieOpt = movieService.getMovieById(id);
            if (movieOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với ID: " + id);
                return "redirect:/admin/movies/list";
            }

            MovieDto movie = movieOpt.get();
            model.addAttribute("movie", movie);

            // Add all directors and actors for autocomplete functionality
            model.addAttribute("allDirectors", directorService.getAllDirectors());
            model.addAttribute("allActors", actorService.getAllActors());

            return "admin/admin_movie_edit";
        } catch (Exception e) {
            System.err.println("Error loading movie for edit: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin phim");
            return "redirect:/admin/movies/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateMovie(@PathVariable Integer id, Model model,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("duration") int duration,
            @RequestParam("rating") String rating,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "genres", required = false) List<String> genres,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "selectedDirectors", required = false) List<Integer> selectedDirectorIds,
            @RequestParam(value = "selectedActors", required = false) List<Integer> selectedActorIds,
            RedirectAttributes redirectAttributes) {

        try {
            // Get existing movie
            Optional<MovieDto> existingMovieOpt = movieService.getMovieById(id);
            if (existingMovieOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với ID: " + id);
                return "redirect:/admin/movies/list";
            }

            MovieDto movie = existingMovieOpt.get();

            // Update basic information
            movie.setName(name);
            movie.setDescription(description);
            movie.setDuration(duration);
            movie.setRating(rating);
            movie.setLanguage("Vietnamese"); // Keep default language

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse(releaseDate);
            Date sqlReleaseDate = new Date(utilDate.getTime());
            movie.setReleaseDate(sqlReleaseDate);

            if (genres != null && !genres.isEmpty()) {
                String genreString = String.join(", ", genres);
                movie.setGenre(genreString);
            }

            // Handle image upload (only if new image is provided)
            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR + fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                movie.setImage("/uploads/movies/" + fileName);
            }
            // If no new image provided, keep existing image

            if (trailerUrl != null && !trailerUrl.trim().isEmpty()) {
                movie.setTrailer(trailerUrl);
            }

            // Handle directors
            if (selectedDirectorIds != null && !selectedDirectorIds.isEmpty()) {
                Set<String> directorNames = selectedDirectorIds.stream()
                        .map(directorService::getDirectorById)
                        .filter(director -> director != null)
                        .map(Director::getName)
                        .collect(Collectors.toSet());
                movie.setDirectors(directorNames);
            } else {
                movie.setDirectors(new HashSet<>());
            }

            // Handle actors
            if (selectedActorIds != null && !selectedActorIds.isEmpty()) {
                Set<String> actorNames = selectedActorIds.stream()
                        .map(actorService::getActorById)
                        .filter(actor -> actor != null)
                        .map(Actor::getName)
                        .collect(Collectors.toSet());
                movie.setActors(actorNames);
            } else {
                movie.setActors(new HashSet<>());
            }

            movieService.saveOrUpdate(movie);

            System.out.println("Movie updated successfully: " + movie.getName());
            redirectAttributes.addFlashAttribute("success", "Cập nhật phim thành công!");

        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Định dạng ngày không hợp lệ");
            return "redirect:/admin/movies/edit/" + id;
        } catch (IOException e) {
            System.err.println("Error uploading image: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải lên hình ảnh");
            return "redirect:/admin/movies/edit/" + id;
        } catch (Exception e) {
            System.err.println("Error updating movie: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật phim");
            return "redirect:/admin/movies/edit/" + id;
        }

        return "redirect:/admin/movies/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<MovieDto> movieOpt = movieService.getMovieById(id);
            if (movieOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với ID: " + id);
                return "redirect:/admin/movies/list";
            }

            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phim thành công!");

        } catch (Exception e) {
            System.err.println("Error deleting movie: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi xóa phim. Phim có thể đang được sử dụng trong hệ thống.");
        }
        return "redirect:/admin/movies/list";
    }

}