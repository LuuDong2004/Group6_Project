package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.repository.Admin.AdminActorRepository;
import group6.cinema_project.repository.Admin.AdminDirectorRepository;
import group6.cinema_project.repository.Admin.AdminMovieRepository;
import group6.cinema_project.repository.Admin.AdminRatingRepository;
import group6.cinema_project.repository.Admin.AdminGenreRepository;
import group6.cinema_project.service.Admin.IAdminMovieService;
import org.springframework.stereotype.Service;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.entity.Rating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMovieServiceImpl implements IAdminMovieService {
    private final AdminMovieRepository adminMovieRepository;
    private final AdminActorRepository actorRepository; 
    private final AdminDirectorRepository directorRepository;
    private final AdminRatingRepository ratingRepository;
    private final AdminGenreRepository genreRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<MovieDto> getMovieById(Integer id) {
        return adminMovieRepository.findById(id)
                .map(this::convertToBasicDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MovieDto> getMovieByIdForDisplay(Integer id) {
        return adminMovieRepository.findByIdWithDirectorsAndActors(id)
                .map(this::convertToDisplayDto);
    }

    @Override
    @Transactional
    public MovieDto saveOrUpdate(MovieDto movieDto) {
        Movie movie = modelMapper.map(movieDto, Movie.class);

        // Xử lý Rating
        if (movieDto.getRatingId() != null) {
            Rating rating = ratingRepository.findById(movieDto.getRatingId()).orElse(null);
            movie.setRating(rating);
        }

        // Xử lý Genres
        if (movieDto.getGenreIds() != null && !movieDto.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>();
            for (Integer genreId : movieDto.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId).orElse(null);
                if (genre != null) {
                    genres.add(genre);
                }
            }
            movie.setGenres(genres);
        }

        // Xử lý directors
        if (movieDto.getDirectors() != null && !movieDto.getDirectors().isEmpty()) {
            Set<Director> directors = new HashSet<>();
            for (String directorName : movieDto.getDirectors()) {
                Director director = directorRepository.findFirstByName(directorName).orElse(null);
                if (director == null) {
                    try {
                        director = new Director();
                        director.setName(directorName);
                        director = directorRepository.save(director);
                    } catch (Exception e) {
                        // If save fails due to duplicate, try to find again
                        director = directorRepository.findFirstByName(directorName).orElse(null);
                        if (director == null) {
                            throw new RuntimeException("Failed to create or find director: " + directorName, e);
                        }
                    }
                }
                directors.add(director);
            }
            movie.setDirectors(directors);
        }

        // Xử lý actors
        if (movieDto.getActors() != null && !movieDto.getActors().isEmpty()) {
            Set<Actor> actors = new HashSet<>();
            for (String actorName : movieDto.getActors()) {
                Actor actor = actorRepository.findFirstByName(actorName).orElse(null);
                if (actor == null) {
                    try {
                        actor = new Actor();
                        actor.setName(actorName);
                        actor = actorRepository.save(actor);
                    } catch (Exception e) {
                        // If save fails due to duplicate, try to find again
                        actor = actorRepository.findFirstByName(actorName).orElse(null);
                        if (actor == null) {
                            throw new RuntimeException("Failed to create or find actor: " + actorName, e);
                        }
                    }
                }
                actors.add(actor);
            }
            movie.setActors(actors);
        }

        Movie savedMovie = adminMovieRepository.save(movie);
        return convertToBasicDto(savedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Integer id) {
        if (!adminMovieRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Movie not found with ID: " + id);
        }
        adminMovieRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovie() {
        return adminMovieRepository.findAll().stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMoviesForDisplay() {
        return adminMovieRepository.findAllWithDirectorsAndActors().stream()
                .map(this::convertToDisplayDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getFilteredMoviesForDisplay(String searchTerm, String filterBy) {
        List<Movie> movies;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            movies = adminMovieRepository.findAllWithDirectorsAndActors();
        } else {
            switch (filterBy.toLowerCase()) {
                case "name":
                case "title":
                    movies = adminMovieRepository
                            .findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "description":
                    movies = adminMovieRepository
                            .findByDescriptionContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "genre":
                    movies = adminMovieRepository
                            .findByGenreContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "rating":
                    movies = adminMovieRepository
                            .findByRatingContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "language":
                    movies = adminMovieRepository
                            .findByLanguageContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "releaseyear":
                case "release_year":
                    try {
                        Integer year = Integer.parseInt(searchTerm.trim());
                        movies = adminMovieRepository.findByReleaseYearWithDirectorsAndActors(year);
                    } catch (NumberFormatException e) {
                        movies = List.of(); // Return empty list if year is not a valid number
                    }
                    break;
                case "director":
                case "directors":
                    movies = adminMovieRepository
                            .findByDirectorNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "actor":
                case "actors":
                    movies = adminMovieRepository
                            .findByActorNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                default:
                    // Default to searching by name if filterBy is not recognized
                    movies = adminMovieRepository
                            .findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
            }
        }

        return movies.stream()
                .map(this::convertToDisplayDto)
                .collect(Collectors.toList());
    }


    private MovieDto convertToBasicDto(Movie movie) {
        MovieDto dto = new MovieDto();

        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());

        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
        }

        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);
        }

        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer()); // Note: entity has 'trailer', DTO has 'trailer'

        return dto;
    }

    private MovieDto convertToDisplayDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map basic fields
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());

        // Xử lý Rating - lấy ID
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
        }

        // Xử lý Genres - lấy ID
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

        // Map directors and actors (these should be loaded via JOIN FETCH)
        if (movie.getDirectors() != null) {
            Set<String> directorNames = movie.getDirectors().stream()
                    .map(Director::getName)
                    .collect(Collectors.toSet());
            dto.setDirectors(directorNames);
        }

        if (movie.getActors() != null) {
            Set<String> actorNames = movie.getActors().stream()
                    .map(Actor::getName)
                    .collect(Collectors.toSet());
            dto.setActors(actorNames);
        }

        // Set thông tin hiển thị cho Rating và Genre
        if (movie.getRating() != null) {
            dto.setRatingDisplay(movie.getRating().getCode() + " - " + movie.getRating().getDescription());
        }

        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            String genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            dto.setGenreDisplay(genreNames);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDto> getAllMoviesForDisplayWithPagination(Pageable pageable) {
        Page<Movie> moviePage = adminMovieRepository.findAllWithDirectorsAndActorsPageable(pageable);
        return moviePage.map(this::convertToDisplayDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDto> getFilteredMoviesForDisplayWithPagination(String searchTerm, String filterBy,
            Pageable pageable) {
        Page<Movie> moviePage;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            moviePage = adminMovieRepository.findAllWithDirectorsAndActorsPageable(pageable);
        } else {
            switch (filterBy.toLowerCase()) {
                case "name":
                case "title":
                    moviePage = adminMovieRepository
                            .findByNameContainingIgnoreCaseWithDirectorsAndActorsPageable(searchTerm.trim(), pageable);
                    break;
                case "description":
                    moviePage = adminMovieRepository
                            .findByDescriptionContainingIgnoreCaseWithDirectorsAndActorsPageable(searchTerm.trim(),
                                    pageable);
                    break;
                case "genre":
                    moviePage = adminMovieRepository
                            .findByGenreContainingIgnoreCaseWithDirectorsAndActorsPageable(searchTerm.trim(), pageable);
                    break;
                case "rating":
                    moviePage = adminMovieRepository.findByRatingContainingIgnoreCaseWithDirectorsAndActorsPageable(
                            searchTerm.trim(), pageable);
                    break;
                case "language":
                    moviePage = adminMovieRepository.findByLanguageContainingIgnoreCaseWithDirectorsAndActorsPageable(
                            searchTerm.trim(), pageable);
                    break;
                case "releaseyear":
                    moviePage = adminMovieRepository.findByReleaseYearWithDirectorsAndActorsPageable(searchTerm.trim(),
                            pageable);
                    break;
                case "director":
                    moviePage = adminMovieRepository
                            .findByDirectorNameContainingIgnoreCaseWithDirectorsAndActorsPageable(searchTerm.trim(),
                                    pageable);
                    break;
                case "actor":
                    moviePage = adminMovieRepository.findByActorNameContainingIgnoreCaseWithDirectorsAndActorsPageable(
                            searchTerm.trim(), pageable);
                    break;
                default:
                    moviePage = adminMovieRepository
                            .findByNameContainingIgnoreCaseWithDirectorsAndActorsPageable(searchTerm.trim(), pageable);
                    break;
            }
        }

        return moviePage.map(this::convertToDisplayDto);
    }

}
