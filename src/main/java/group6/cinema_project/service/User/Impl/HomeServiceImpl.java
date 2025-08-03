package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getPopularMovies() {
        List<Movie> movies = movieRepository.findTop8ByOrderByRatingDesc();
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }
        System.out.println("Found " + movies.size() + " movies for popular");
        return movies.stream().map(this::convertToMovieDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getNewReleases() {
        List<Movie> movies = movieRepository.findTop8ByOrderByReleaseDateDesc();
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }
        System.out.println("Found " + movies.size() + " movies for new releases");
        return movies.stream().map(this::convertToMovieDto).collect(Collectors.toList());
    }

    private MovieDto convertToMovieDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        // Sửa đường dẫn hình ảnh nếu cần
        String imagePath = movie.getImage();
        if (imagePath != null && !imagePath.startsWith("/assets/")) {
            imagePath = "/assets/images/" + imagePath;
        }
        dto.setImage(imagePath);
        dto.setDuration(movie.getDuration());
        dto.setReleaseDate(movie.getReleaseDate());

        // Xử lý Rating - lấy ID và set display text
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
            dto.setRatingDisplay(movie.getRating().getCode() + " - " + movie.getRating().getDescription());
        }

        // Xử lý Genres - chuyển đổi thành Set<Integer> genreIds
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);

            // Tạo genreDisplay để hiển thị
            String genreDisplay = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            dto.setGenreDisplay(genreDisplay);
        }

        dto.setLanguage(movie.getLanguage());
        dto.setTrailer(movie.getTrailer());
        dto.setDescription(movie.getDescription());
        dto.setStatus(movie.getStatus());

        // Map actors
        if (movie.getActors() != null) {
            dto.setActors(movie.getActors().stream()
                    .map(actor -> new PersonSimpleDto(actor.getId(), actor.getName(), actor.getImageUrl()))
                    .collect(Collectors.toList()));
        }

        // Map directors
        if (movie.getDirectors() != null) {
            dto.setDirectors(movie.getDirectors().stream()
                    .map(director -> new PersonSimpleDto(director.getId(), director.getName(), director.getImageUrl()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

}