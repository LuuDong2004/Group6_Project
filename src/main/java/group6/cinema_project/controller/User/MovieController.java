
package group6.cinema_project.controller.User;

import group6.cinema_project.dto.CustomerMovieDto;
import group6.cinema_project.service.User.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import group6.cinema_project.dto.BookingDto;
import group6.cinema_project.service.User.IBookingService;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("movie")
public class MovieController {
    @Autowired
    IMovieService movieService;
    @Autowired
    private IBookingService bookingService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("view")
    public String getAllMoviesAndByGenre(@RequestParam(value = "genre", required = false) String genre, Model model) {
        List<CustomerMovieDto> allMovies = movieService.getAllMovie();
        List<CustomerMovieDto> topMovies = movieService.getTopMovies7Days();
        model.addAttribute("Movies", allMovies);
        model.addAttribute("topMovies", topMovies);
        // Lấy recommendedMovies cho user hiện tại
        List<CustomerMovieDto> recommendedMovies = recommendMoviesInternal();
        model.addAttribute("recommendedMovies", recommendedMovies);
        // Thêm timestamp để tránh cache hình ảnh
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "movies";
    }

    // Hàm dùng để lấy recommendedMovies cho user hiện tại
    private List<CustomerMovieDto> recommendMoviesInternal() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getName().equals("anonymousUser")) {
                return movieService.getTopMovies7Days();
            }
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return movieService.getTopMovies7Days();
            }
            List<BookingDto> bookings = bookingService.getBookingsByUserId(user.getId());
            if (bookings == null || bookings.isEmpty()) {
                return movieService.getTopMovies7Days();
            }
            Set<String> genres = new HashSet<>();
            Set<Integer> watchedMovieIds = new HashSet<>();
            for (BookingDto booking : bookings) {
                if (booking.getSchedule() != null) {
                    Integer movieId = null;
                    String genre = null;
                    if (booking.getSchedule().getMovieId() != null) {
                        movieId = booking.getSchedule().getMovieId();
                    } else if (booking.getSchedule().getMovie() != null
                            && booking.getSchedule().getMovie().getId() != null) {
                        movieId = booking.getSchedule().getMovie().getId();
                    }
                    if (movieId != null) {
                        watchedMovieIds.add(movieId);
                    }
                    if (booking.getSchedule().getMovieGenre() != null) {
                        genre = booking.getSchedule().getMovieGenre();
                    } else if (booking.getSchedule().getMovie() != null
                            && booking.getSchedule().getMovie().getGenreDisplay() != null) {
                        genre = booking.getSchedule().getMovie().getGenreDisplay();
                    }
                    if (genre != null) {
                        String[] splitGenres = genre.split(",");
                        for (String g : splitGenres) {
                            genres.add(g.trim());
                        }
                    }
                }
            }
            List<CustomerMovieDto> allMovies = movieService.getAllMovie();
            List<CustomerMovieDto> recommend = new ArrayList<>();
            for (CustomerMovieDto movie : allMovies) {
                if (movie.getGenreDisplay() != null && !watchedMovieIds.contains(movie.getId())) {
                    for (String g : genres) {
                        if (movie.getGenreDisplay().toLowerCase().contains(g.toLowerCase())) {
                            recommend.add(movie);
                            break;
                        }
                    }
                }
            }
            recommend.sort((m1, m2) -> {
                String rating1 = m1.getRatingDisplay() != null ? m1.getRatingDisplay() : "";
                String rating2 = m2.getRatingDisplay() != null ? m2.getRatingDisplay() : "";
                return rating2.compareTo(rating1);
            });
            if (recommend.size() > 5)
                recommend = recommend.subList(0, 5);
            if (recommend.isEmpty()) {
                recommend = movieService.getTopMovies7Days();
            }
            return recommend;
        } catch (Exception e) {
            return movieService.getTopMovies7Days();
        }
    }

    @GetMapping("/loadMore")
    @ResponseBody
    public ResponseEntity<List<CustomerMovieDto>> loadMoreMovies(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {
        List<CustomerMovieDto> movies = movieService.getMoviesWithPagination(page, size);
        return ResponseEntity.ok(movies);
    }

    // API endpoint để load tất cả phim
    @GetMapping("/loadAll")
    @ResponseBody
    public ResponseEntity<List<CustomerMovieDto>> loadAllMovies() {
        List<CustomerMovieDto> allMovies = movieService.getAllMovie();
        return ResponseEntity.ok(allMovies);
    }

    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> filterMovies(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        List<CustomerMovieDto> movies = movieService.filterMovies(genre, null, sort, search);
        int pageSize = 8;
        int total = movies.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CustomerMovieDto> pageMovies = (fromIndex < total) ? movies.subList(fromIndex, toIndex) : List.of();
        Map<String, Object> result = new HashMap<>();
        result.put("movies", pageMovies);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/genres")
    @ResponseBody
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = movieService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/recommend")
    @ResponseBody
    public ResponseEntity<List<CustomerMovieDto>> recommendMovies() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getName().equals("anonymousUser")) {
                // Nếu chưa đăng nhập, trả về top phim rating cao nhất gần đây
                List<CustomerMovieDto> topMovies = movieService.getTopMovies7Days();
                return ResponseEntity.ok(topMovies);
            }
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                List<CustomerMovieDto> topMovies = movieService.getTopMovies7Days();
                return ResponseEntity.ok(topMovies);
            }
            // Lấy các booking gần nhất
            List<BookingDto> bookings = bookingService.getBookingsByUserId(user.getId());
            if (bookings == null || bookings.isEmpty()) {
                List<CustomerMovieDto> topMovies = movieService.getTopMovies7Days();
                return ResponseEntity.ok(topMovies);
            }
            // Lấy các thể loại từ các booking gần nhất
            Set<String> genres = new HashSet<>();
            Set<Integer> watchedMovieIds = new HashSet<>();
            for (BookingDto booking : bookings) {
                if (booking.getSchedule() != null) {
                    Integer movieId = null;
                    String genre = null;
                    // Ưu tiên lấy movieId từ schedule.movieId, nếu không có thì lấy từ
                    // schedule.movie.id
                    if (booking.getSchedule().getMovieId() != null) {
                        movieId = booking.getSchedule().getMovieId();
                    } else if (booking.getSchedule().getMovie() != null
                            && booking.getSchedule().getMovie().getId() != null) {
                        movieId = booking.getSchedule().getMovie().getId();
                    }
                    if (movieId != null) {
                        watchedMovieIds.add(movieId);
                    }
                    // Lấy genre từ schedule.movieGenre hoặc schedule.movie.genre
                    if (booking.getSchedule().getMovieGenre() != null) {
                        genre = booking.getSchedule().getMovieGenre();
                    } else if (booking.getSchedule().getMovie() != null
                            && booking.getSchedule().getMovie().getGenreDisplay() != null) {
                        genre = booking.getSchedule().getMovie().getGenreDisplay();
                    }
                    if (genre != null) {
                        String[] splitGenres = genre.split(",");
                        for (String g : splitGenres) {
                            genres.add(g.trim());
                        }
                    }
                }
            }
            // Gợi ý phim cùng thể loại, loại trừ phim đã xem
            List<CustomerMovieDto> allMovies = movieService.getAllMovie();
            List<CustomerMovieDto> recommend = new ArrayList<>();
            for (CustomerMovieDto movie : allMovies) {
                if (movie.getGenreDisplay() != null && !watchedMovieIds.contains(movie.getId())) {
                    for (String g : genres) {
                        if (movie.getGenreDisplay().toLowerCase().contains(g.toLowerCase())) {
                            recommend.add(movie);
                            break;
                        }
                    }
                }
            }
            // Sắp xếp theo rating giảm dần (sử dụng ratingDisplay)
            recommend.sort((m1, m2) -> {
                String rating1 = m1.getRatingDisplay() != null ? m1.getRatingDisplay() : "";
                String rating2 = m2.getRatingDisplay() != null ? m2.getRatingDisplay() : "";
                return rating2.compareTo(rating1);
            });
            // Lấy tối đa 5 phim
            if (recommend.size() > 5)
                recommend = recommend.subList(0, 5);
            // Nếu không có phim nào phù hợp, fallback top rating
            if (recommend.isEmpty()) {
                recommend = movieService.getTopMovies7Days();
            }
            return ResponseEntity.ok(recommend);
        } catch (Exception e) {
            // Nếu lỗi, fallback top rating
            List<CustomerMovieDto> topMovies = movieService.getTopMovies7Days();
            return ResponseEntity.ok(topMovies);
        }
    }

    @GetMapping("/view/{id}")
    public String getMovieDetail(@PathVariable Integer id, Model model) {
        CustomerMovieDto movie = movieService.getMovieDetail(id);
        if (movie == null) {
            return "redirect:/movie/view";
        }
        model.addAttribute("movie", movie);
        return "movie_detail";
    }
}
