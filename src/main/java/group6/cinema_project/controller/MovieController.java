package group6.cinema_project.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.service.ActorService;
import group6.cinema_project.service.DirectorService;
import group6.cinema_project.service.impl.MovieServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieServiceImpl movieService;
    private final ActorService actorService;
    private final DirectorService directorService;

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
    public String listMovies(Model model) {
        List<MovieDto> movies = movieService.getAllMovie();
        model.addAttribute("movies", movies); // Thêm dòng này
        return "admin/admin_movie_list"; // Nếu dùng Thymeleaf, chỉ cần trả về "index"
    }

    @GetMapping("/add")
    public String adminMovieAdd(Model model) {
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
            @RequestParam(value = "director", required = false) String director,
            @RequestParam(value = "cast", required = false) String cast) {

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
            if (director != null && !director.trim().isEmpty()) {
                Set<Director> directors = directorService.findOrCreateDirectors(director);
                Set<String> directorNames = directors.stream()
                        .map(Director::getName)
                        .collect(Collectors.toSet());
                movie.setDirectors(directorNames);
            }

            if (cast != null && !cast.trim().isEmpty()) {
                Set<Actor> actors = actorService.findOrCreateActors(cast);
                Set<String> actorNames = actors.stream()
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
}
