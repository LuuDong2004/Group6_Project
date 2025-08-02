package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.repository.Admin.AdminActorRepository;
import group6.cinema_project.repository.Admin.AdminDirectorRepository;
import group6.cinema_project.repository.Admin.AdminMovieRepository;
import group6.cinema_project.service.Admin.IAdminMovieService;
import org.springframework.stereotype.Service;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;



import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminMoiveServiceImpl implements IAdminMovieService {
    private final AdminMovieRepository adminMovieRepository;
    private final AdminActorRepository actorRepository; // Thêm repo này
    private final AdminDirectorRepository directorRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<MovieDto> getMovieById(Integer id) {
        return adminMovieRepository.findById(id)
                .map(movie -> modelMapper.map(movie, MovieDto.class));
    }

    @Override
    @Transactional
    public MovieDto saveOrUpdate(MovieDto movieDto) {
        Movie movie = modelMapper.map(movieDto, Movie.class);

        // Xử lý directors
        if (movieDto.getDirectors() != null && !movieDto.getDirectors().isEmpty()) {
            Set<Director> directors = new HashSet<>();
            for (PersonSimpleDto directorDto : movieDto.getDirectors()) {
                Director director = directorRepository.findById(directorDto.getId()).orElse(null);
                if (director == null) {
                    // Nếu không tìm thấy theo ID, thử tìm theo tên
                    director = directorRepository.findFirstByName(directorDto.getName()).orElse(null);
                    if (director == null) {
                        try {
                            director = new Director();
                            director.setName(directorDto.getName());
                            director.setImageUrl(directorDto.getImageUrl());
                            director = directorRepository.save(director);
                        } catch (Exception e) {
                            // If save fails due to duplicate, try to find again
                            director = directorRepository.findFirstByName(directorDto.getName()).orElse(null);
                            if (director == null) {
                                throw new RuntimeException("Failed to create or find director: " + directorDto.getName(), e);
                            }
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
            for (PersonSimpleDto actorDto : movieDto.getActors()) {
                Actor actor = actorRepository.findById(actorDto.getId()).orElse(null);
                if (actor == null) {
                    // Nếu không tìm thấy theo ID, thử tìm theo tên
                    actor = actorRepository.findFirstByName(actorDto.getName()).orElse(null);
                    if (actor == null) {
                        try {
                            actor = new Actor();
                            actor.setName(actorDto.getName());
                            actor.setImageUrl(actorDto.getImageUrl());
                            actor = actorRepository.save(actor);
                        } catch (Exception e) {
                            // If save fails due to duplicate, try to find again
                            actor = actorRepository.findFirstByName(actorDto.getName()).orElse(null);
                            if (actor == null) {
                                throw new RuntimeException("Failed to create or find actor: " + actorDto.getName(), e);
                            }
                        }
                    }
                }
                actors.add(actor);
            }
            movie.setActors(actors);
        }

        Movie savedMovie = adminMovieRepository.save(movie);
        return modelMapper.map(savedMovie, MovieDto.class);
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
                    movies = adminMovieRepository.findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "description":
                    movies = adminMovieRepository
                            .findByDescriptionContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "genre":
                    movies = adminMovieRepository.findByGenreContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
                    break;
                case "rating":
                    movies = adminMovieRepository.findByRatingContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
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
                    movies = adminMovieRepository.findByNameContainingIgnoreCaseWithDirectorsAndActors(searchTerm.trim());
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
            List<PersonSimpleDto> directors = movie.getDirectors().stream()
                    .map(director -> new PersonSimpleDto(director.getId(), director.getName(), director.getImageUrl()))
                    .collect(Collectors.toList());
            dto.setDirectors(directors);
        }

        if (movie.getActors() != null) {
            List<PersonSimpleDto> actors = movie.getActors().stream()
                    .map(actor -> new PersonSimpleDto(actor.getId(), actor.getName(), actor.getImageUrl()))
                    .collect(Collectors.toList());
            dto.setActors(actors);
        }

        return dto;
    }


}
