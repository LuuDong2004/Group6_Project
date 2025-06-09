package group6.cinema_project.service.impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.ActorRepository;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.service.MovieService;
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
@Transactional
public class MovieServiceImpl implements MovieService {

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
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

}