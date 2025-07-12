package group6.cinema_project.controller;

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

import group6.cinema_project.dto.BranchDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.FoodDto;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.UserRegistrationDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Branch;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Role;
import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import group6.cinema_project.service.ActorService;
import group6.cinema_project.service.BranchService;
import group6.cinema_project.service.DirectorService;
import group6.cinema_project.service.FoodService;
import group6.cinema_project.service.UserService;
import group6.cinema_project.service.impl.MovieServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MovieServiceImpl movieService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final FoodService foodService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BranchService branchService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/movies/";
    private final String FOOD_UPLOAD_DIR = "src/main/resources/static/food/";

    @GetMapping("/login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {

        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }

        return "admin_login";
    }

    @GetMapping("/staff/login")
    public String staffLoginPage() {
        return "redirect:/admin/login";
    }

    @GetMapping
    public String adminDashboard() {
        return "admin_dashboard";
    }

    // Method để tạo tài khoản staff (chỉ dùng để test)
    @GetMapping("/create-staff")
    public String createStaffAccount() {
        try {
            // Tạo UserRegistrationDto cho staff
            UserRegistrationDto staffDto = new UserRegistrationDto();
            staffDto.setUserName("Staff User");
            staffDto.setEmail("staff@cinema.com");
            staffDto.setPassword("123456");
            staffDto.setConfirmPassword("123456");
            staffDto.setPhone("0123456789");
            staffDto.setDateOfBirth("1990-01-01");
            staffDto.setAddress("Hà Nội");
            
            // Tạo user với role STAFF
            User user = new User(
                staffDto.getUserName(),
                staffDto.getPhone(),
                staffDto.getEmail(),
                passwordEncoder.encode(staffDto.getPassword()),
                staffDto.getDateOfBirth(),
                staffDto.getAddress(),
                Role.STAFF
            );
            
            userRepository.save(user);
            return "redirect:/admin?message=Staff account created successfully";
        } catch (Exception e) {
            return "redirect:/admin?error=Failed to create staff account: " + e.getMessage();
        }
    }

    // Movie management functions
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

    @GetMapping("/movies/list")
    public String listMovies(Model model) {
        List<MovieDto> movies = movieService.getAllMovie();
        model.addAttribute("movies", movies);
        return "admin_movie_list";
    }

    @GetMapping("/movies/add")
    public String adminMovieAdd(Model model) {
        model.addAttribute("allDirectors", directorService.getAllDirectors());
        model.addAttribute("allActors", actorService.getAllActors());
        return "admin/admin_movie_add";
    }

    @PostMapping("/movies/add")
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
            movie.setLanguage("Vietnamese");

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

    @GetMapping("/movies/edit/{id}")
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

    @PostMapping("/movies/edit/{id}")
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
            movie.setLanguage("Vietnamese");

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

    @GetMapping("/movies/delete/{id}")
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

    @GetMapping("/foods/list")
    public String listFoods(Model model) {
        model.addAttribute("foods", foodService.getAllFoods());
        return "admin_food_list";
    }
    @GetMapping("/foods/add")
    public String addFoodForm(Model model) {
        model.addAttribute("food", new FoodDto());
        return "admin_food_add";
    }

    // Handle add
    @PostMapping("/foods/add")
    public String addFood(@ModelAttribute FoodDto foodDto, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(FOOD_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                foodDto.setImage("/food/" + fileName);
            }
            foodService.saveOrUpdate(foodDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/foods/list";
    }
    // Show edit form
    @GetMapping("/foods/edit/{id}")
    public String editFoodForm(@PathVariable Integer id, Model model) {
        FoodDto food = foodService.getFoodById(id);
        model.addAttribute("food", food);
        return "admin_food_edit";
    }

    // Handle edit
    @PostMapping("/foods/edit/{id}")
    public String editFood(@PathVariable Integer id, @ModelAttribute FoodDto foodDto, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(FOOD_UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                foodDto.setImage("/food/" + fileName);
            } else {
                // giữ lại ảnh cũ nếu không upload mới
                FoodDto old = foodService.getFoodById(id);
                foodDto.setImage(old.getImage());
            }
            foodDto.setId(id);
            foodService.saveOrUpdate(foodDto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/foods/list";
    }

    // Delete
    @GetMapping("/foods/delete/{id}")
    public String deleteFood(@PathVariable Integer id) {
        foodService.deleteFood(id);
        return "redirect:/admin/foods/list";
    }
    
//    // --- Branch Management ---
//    @GetMapping("/branches/list")
//    public String listBranches(Model model) {
//        model.addAttribute("branches", branchService.findAll());
//        model.addAttribute("branch", new Branch());
//        return "admin_branch_management";
//    }
//
//    @PostMapping("/branches/add")
//    public String addBranch(@ModelAttribute BranchDto branchDto, RedirectAttributes redirectAttributes) {
//        branchService.save(branchDto);
//        redirectAttributes.addFlashAttribute("success", "Thêm chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
//    @GetMapping("/branches/edit/{id}")
//    public String editBranchForm(@PathVariable Integer id, Model model) {
//        Branch branch = branchService.findById(id);
//        model.addAttribute("branch", branch);
//        return "admin_branch_edit";
//    }
//
//    @PostMapping("/branches/edit/{id}")
//    public String editBranch(@PathVariable Integer id, @ModelAttribute Branch branch, RedirectAttributes redirectAttributes) {
//        branch.setId(id);
//        branchService.saveOrUpdate(branch);
//        redirectAttributes.addFlashAttribute("success", "Cập nhật chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
//    @GetMapping("/branches/delete/{id}")
//    public String deleteBranch(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
//        branchService.deleteBranch(id);
//        redirectAttributes.addFlashAttribute("success", "Xóa chi nhánh thành công!");
//        return "redirect:/admin/branches/list";
//    }
//
}
