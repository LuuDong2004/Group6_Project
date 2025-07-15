package group6.cinema_project.service.impl;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.repository.ActorRepository;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.service.ActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ActorDirectorServiceImpl implements ActorDirectorService {
    @Autowired private ActorRepository actorRepository;
    @Autowired private DirectorRepository directorRepository;
    @Autowired private ActorMovieRepository actorMovieRepository;
    @Autowired private MovieRepository movieRepository;

    @Override
    public List<Actor> getAllActors() { return actorRepository.findAll(); }
    @Override
    public List<Director> getAllDirectors() { return directorRepository.findAll(); }
    @Override
    public Actor getActorById(Long id) { return actorRepository.findById(id).orElse(null); }
    @Override
    public Director getDirectorById(Long id) { return directorRepository.findById(id).orElse(null); }
    @Override
    public List<Actor> getActorsByMovieId(Long movieId) {
        return actorMovieRepository.findByMovieId(movieId).stream().map(ActorMovie::getActor).collect(Collectors.toList());
    }
    
    @Override
    public List<Director> getDirectorsByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie != null && movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
            return new ArrayList<>(movie.getDirectors());
        }
        return List.of();
    }
    
    // DTO methods
    @Override
    public List<ActorDto> getAllActorDTOs() {
        return actorRepository.findAll().stream().map(this::convertToActorDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<DirectorDto> getAllDirectorDTOs() {
        return directorRepository.findAll().stream().map(this::convertToDirectorDTO).collect(Collectors.toList());
    }
    
    @Override
    public ActorDto getActorDTOById(Long id) {
        Actor actor = actorRepository.findById(id).orElse(null);
        return actor != null ? convertToActorDTO(actor) : null;
    }
    
    @Override
    public DirectorDto getDirectorDTOById(Long id) {
        Director director = directorRepository.findById(id).orElse(null);
        return director != null ? convertToDirectorDTO(director) : null;
    }
    
    @Override
    public List<ActorDto> getActorDTOsByMovieId(Long movieId) {
        return getActorsByMovieId(movieId).stream().map(this::convertToActorDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<DirectorDto> getDirectorDTOsByMovieId(Long movieId) {
        // Nếu không còn liên kết nhiều-nhiều, trả về rỗng hoặc lấy theo quan hệ mới nếu có
        return List.of();
    }
    
    private ActorDto convertToActorDTO(Actor actor) {
        ActorDto dto = new ActorDto();
        dto.id = actor.getId();
        dto.name = actor.getName();
        dto.image = actor.getImage();
        dto.biography = actor.getDescription(); // Using description as biography
        dto.birthDate = null; // birthDate field doesn't exist in Actor entity
        dto.nationality = null; // nationality field doesn't exist in Actor entity
        return dto;
    }
    
    private DirectorDto convertToDirectorDTO(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.id = director.getId();
        dto.name = director.getName();
        dto.image = director.getImage();
        dto.biography = director.getDescription(); // Using description as biography
        dto.birthDate = null; // birthDate field doesn't exist in Director entity
        dto.nationality = null; // nationality field doesn't exist in Director entity
        return dto;
    }
} 