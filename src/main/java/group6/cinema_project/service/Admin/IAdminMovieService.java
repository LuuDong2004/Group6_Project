package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.MovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IAdminMovieService {
    Optional<MovieDto> getMovieById(Integer id);

    MovieDto saveOrUpdate(MovieDto movieDto);

    void deleteMovie(Integer id);

    // Soft delete restore method - Phương thức khôi phục phim đã bị soft delete
    void restoreMovie(Integer id);

    List<MovieDto> getAllMovie();

    // New methods for display with directors and actors
    List<MovieDto> getAllMoviesForDisplay();

    List<MovieDto> getFilteredMoviesForDisplay(String searchTerm, String filterBy);

    // Pagination methods
    Page<MovieDto> getAllMoviesForDisplayWithPagination(Pageable pageable);

    Page<MovieDto> getFilteredMoviesForDisplayWithPagination(String searchTerm, String filterBy, Pageable pageable);
}
