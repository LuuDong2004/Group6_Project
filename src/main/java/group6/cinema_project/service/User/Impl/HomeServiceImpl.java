package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IHomeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        dto.setRating(movie.getRating());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setTrailer(movie.getTrailer());
        dto.setDescription(movie.getDescription());

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