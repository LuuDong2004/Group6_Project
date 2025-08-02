package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IHomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements IHomeService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getPopularMovies() {
        List<Movie> movies = movieRepository.findTop8ByOrderByRatingDesc(PageRequest.of(0, 8));
        System.out.println("Found " + movies.size() + " movies for popular");
        return movies.stream().map(this::convertToBasicDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getNewReleases() {
        List<Movie> movies = movieRepository.findTop8ByOrderByReleaseDateDesc();
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }
        System.out.println("Found " + movies.size() + " movies for new releases");
        return movies.stream().map(this::convertToBasicDto).collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Movie entity sang DTO mà không gây ra vấn đề lazy loading
     * Phương thức này chỉ map các field cơ bản để tránh các vấn đề cascade của
     * ModelMapper
     */
    private MovieDto convertToBasicDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map các field cơ bản
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());

        // Xử lý Rating - chỉ lấy ID để tránh lazy loading
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
        }

        // Xử lý Genres - chỉ lấy ID để tránh lazy loading
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);
        }

        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());
        dto.setStatus(movie.getStatus());

        return dto;
    }
}