package group6.cinema_project.service.impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.ActorRepository;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.service.IMovieService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository; // Thêm repo này
    private final DirectorRepository directorRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<MovieDto> getMovieById(Integer id) {
        return movieRepository.findById(id)
                .map(movie -> modelMapper.map(movie, MovieDto.class));
    }

    @Override
    @Transactional
    public MovieDto saveOrUpdate(MovieDto movieDto) {
        Movie movie = modelMapper.map(movieDto, Movie.class);

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

        Movie savedMovie = movieRepository.save(movie);
        return modelMapper.map(savedMovie, MovieDto.class);
    }

    @Override
    @Transactional
    public void deleteMovie(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovie() {
        return movieRepository.findAll().stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMoviesForDisplay() {
        return movieRepository.findAllWithDirectorsAndActors().stream()
                .map(this::convertToDisplayDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getFilteredMoviesForDisplay(String searchTerm, String filterBy) {
        List<Movie> movies;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            movies = movieRepository.findAllWithDirectorsAndActors();
        } else {
            switch (filterBy.toLowerCase()) {
                case "name":
                case "title":
                    movies = movieRepository.findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "description":
                    movies = movieRepository
                            .findByDescriptionContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "genre":
                    movies = movieRepository.findByGenreContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "rating":
                    movies = movieRepository.findByRatingContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "language":
                    movies = movieRepository
                            .findByLanguageContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "releaseyear":
                case "release_year":
                    try {
                        Integer year = Integer.parseInt(searchTerm.trim());
                        movies = movieRepository.findByReleaseYearWithDirectorsAndActors(year);
                    } catch (NumberFormatException e) {
                        movies = List.of(); // Return empty list if year is not a valid number
                    }
                    break;
                case "director":
                case "directors":
                    movies = movieRepository
                            .findByDirectorNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "actor":
                case "actors":
                    movies = movieRepository
                            .findByActorNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                default:
                    // Default to searching by name if filterBy is not recognized
                    movies = movieRepository.findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
            }
        }

        return movies.stream()
                .map(this::convertToDisplayDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert Movie entity to DTO without triggering relationship loading
     * This method manually maps only the basic fields to avoid ModelMapper cascade
     * issues
     */
    private MovieDto convertToBasicDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map only basic fields that exist in Movie entity
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setRating(movie.getRating());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer()); // Note: entity has 'trailer', DTO has 'trailer'

        // Don't map any relationships or collections to avoid cascade loading
        // Directors and actors will remain empty, which is fine for dropdown purposes

        return dto;
    }

    /**
     * Convert Movie entity to DTO with directors and actors for display purposes
     * This method assumes directors and actors are already loaded (via JOIN FETCH)
     */
    private MovieDto convertToDisplayDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map basic fields
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setRating(movie.getRating());
        dto.setGenre(movie.getGenre());
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

        return dto;
    }

}