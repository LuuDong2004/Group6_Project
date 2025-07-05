package group6.cinema_project.service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.entity.DirectorMovie;
import group6.cinema_project.dto.ActorDTO;
import group6.cinema_project.dto.DirectorDTO;
import group6.cinema_project.repository.ActorRepository;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.DirectorMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorDirectorServiceImpl implements ActorDirectorService {
    @Autowired private ActorRepository actorRepository;
    @Autowired private DirectorRepository directorRepository;
    @Autowired private ActorMovieRepository actorMovieRepository;
    @Autowired private DirectorMovieRepository directorMovieRepository;

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
        return directorMovieRepository.findByMovie_Id(movieId).stream().map(DirectorMovie::getDirector).collect(Collectors.toList());
    }
    
    // DTO methods
    @Override
    public List<ActorDTO> getAllActorDTOs() {
        return actorRepository.findAll().stream().map(this::convertToActorDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<DirectorDTO> getAllDirectorDTOs() {
        return directorRepository.findAll().stream().map(this::convertToDirectorDTO).collect(Collectors.toList());
    }
    
    @Override
    public ActorDTO getActorDTOById(Long id) {
        Actor actor = actorRepository.findById(id).orElse(null);
        return actor != null ? convertToActorDTO(actor) : null;
    }
    
    @Override
    public DirectorDTO getDirectorDTOById(Long id) {
        Director director = directorRepository.findById(id).orElse(null);
        return director != null ? convertToDirectorDTO(director) : null;
    }
    
    @Override
    public List<ActorDTO> getActorDTOsByMovieId(Long movieId) {
        return getActorsByMovieId(movieId).stream().map(this::convertToActorDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<DirectorDTO> getDirectorDTOsByMovieId(Long movieId) {
        return getDirectorsByMovieId(movieId).stream().map(this::convertToDirectorDTO).collect(Collectors.toList());
    }
    
    private ActorDTO convertToActorDTO(Actor actor) {
        ActorDTO dto = new ActorDTO();
        dto.id = actor.getId();
        dto.name = actor.getName();
        dto.image = actor.getImage();
        dto.biography = actor.getBiography();
        dto.birthDate = actor.getBirthDate() != null ? actor.getBirthDate().toString() : null;
        dto.nationality = actor.getNationality();
        return dto;
    }
    
    private DirectorDTO convertToDirectorDTO(Director director) {
        DirectorDTO dto = new DirectorDTO();
        dto.id = director.getId();
        dto.name = director.getName();
        dto.image = director.getImage();
        dto.biography = director.getBiography();
        dto.birthDate = director.getBirthDate() != null ? director.getBirthDate().toString() : null;
        dto.nationality = director.getNationality();
        return dto;
    }
} 