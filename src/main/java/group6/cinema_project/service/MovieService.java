package group6.cinema_project.service;
 // Thay thế bằng package service của bạn

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import group6.cinema_project.entity.Movie; // Thay thế bằng package entity của bạn
import group6.cinema_project.repository.MovieRepository; // Thay thế bằng package repository của bạn

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok sẽ tự động tạo constructor với các trường final/non-null
public class MovieService {

    private final MovieRepository movieRepository; // Lombok injects this via constructor

    /**
     * Lấy tất cả các phim.
     * @return Danh sách tất cả phim.
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Lấy một phim dựa trên ID.
     * @param id ID của phim.
     * @return Optional chứa phim nếu tìm thấy, ngược lại là Optional rỗng.
     */
    public Optional<Movie> getMovieById(Integer id) {
        return movieRepository.findById(id);
    }

    /**
     * Tạo một phim mới.
     * @param movie Đối tượng phim cần tạo.
     * @return Phim đã được tạo.
     */
    public Movie createMovie(Movie movie) {
        // Có thể thêm logic validate dữ liệu đầu vào ở đây trước khi lưu
        return movieRepository.save(movie);
    }

    /**
     * Cập nhật thông tin một phim đã có.
     * @param id ID của phim cần cập nhật.
     * @param movieDetails Đối tượng phim chứa thông tin mới.
     * @return Optional chứa phim đã cập nhật nếu thành công, ngược lại là Optional rỗng (nếu không tìm thấy phim).
     */
    public Optional<Movie> updateMovie(Integer id, Movie movieDetails) {
        return movieRepository.findById(id)
            .map(existingMovie -> {
                existingMovie.setName(movieDetails.getName());
                existingMovie.setImage(movieDetails.getImage());
                existingMovie.setDuration(movieDetails.getDuration());
                existingMovie.setReleaseDate(movieDetails.getReleaseDate());
                existingMovie.setRating(movieDetails.getRating());
                existingMovie.setGenre(movieDetails.getGenre());
                existingMovie.setLanguage(movieDetails.getLanguage());
                existingMovie.setTrailer(movieDetails.getTrailer());
                // Cập nhật các mối quan hệ nếu cần (ví dụ: actors, directors)
                // existingMovie.setActors(movieDetails.getActors());
                // existingMovie.setDirectors(movieDetails.getDirectors());
                return movieRepository.save(existingMovie);
            });
    }

    /**
     * Xóa một phim dựa trên ID.
     * @param id ID của phim cần xóa.
     * @return true nếu xóa thành công, false nếu không tìm thấy phim để xóa.
     */
    public boolean deleteMovie(Integer id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    } 

    
}
