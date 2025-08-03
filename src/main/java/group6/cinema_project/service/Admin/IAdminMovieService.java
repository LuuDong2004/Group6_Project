package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.AdminMovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IAdminMovieService {
    Optional<AdminMovieDto> getMovieById(Integer id);

    // Phương thức lấy thông tin phim để hiển thị (có ratingDisplay và genreDisplay)
    Optional<AdminMovieDto> getMovieByIdForDisplay(Integer id);

    AdminMovieDto saveOrUpdate(AdminMovieDto movieDto);

    void deleteMovie(Integer id);

    List<AdminMovieDto> getAllMovie();

    // New methods for display with directors and actors
    List<AdminMovieDto> getAllMoviesForDisplay();

    List<AdminMovieDto> getFilteredMoviesForDisplay(String searchTerm, String filterBy);

    // Phương thức phân trang cho hiển thị danh sách phim
    Page<AdminMovieDto> getAllMoviesForDisplayWithPagination(Pageable pageable);

    // Phương thức phân trang cho tìm kiếm và lọc phim
    Page<AdminMovieDto> getFilteredMoviesForDisplayWithPagination(String searchTerm, String filterBy,
            Pageable pageable);
}
