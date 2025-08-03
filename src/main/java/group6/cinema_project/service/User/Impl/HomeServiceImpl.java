package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.CustomerMovieDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IHomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerMovieDto> getPopularMovies() {
        List<Movie> movies = movieRepository.findTop8ByOrderByRatingDesc(PageRequest.of(0, 8));
        System.out.println("Found " + movies.size() + " movies for popular");
        return movies.stream().map(this::convertToBasicDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerMovieDto> getNewReleases() {
        List<Movie> movies = movieRepository.findTop8ByOrderByReleaseDateDesc();
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }
        System.out.println("Found " + movies.size() + " movies for new releases");
        return movies.stream().map(this::convertToBasicDto).collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Movie entity sang CustomerMovieDto cho customer viewing
     * Bao gồm ratingDisplay và genreDisplay đã được format
     */
    private CustomerMovieDto convertToBasicDto(Movie movie) {
        CustomerMovieDto dto = new CustomerMovieDto();

        // Map các field cơ bản
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());
        dto.setStatus(movie.getStatus());

        // Set thông tin hiển thị cho Rating
        if (movie.getRating() != null) {
            dto.setRatingDisplay(movie.getRating().getCode() + " - " + movie.getRating().getDescription());
        }

        // Set thông tin hiển thị cho Genre
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            String genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            dto.setGenreDisplay(genreNames);
        }

        return dto;
    }
}