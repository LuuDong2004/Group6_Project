package group6.cinema_project.service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.dto.ActorDTO;
import group6.cinema_project.dto.DirectorDTO;
import java.util.List;

public interface ActorDirectorService {
    List<Actor> getAllActors();
    List<Director> getAllDirectors();
    Actor getActorById(Long id);
    Director getDirectorById(Long id);
    List<Actor> getActorsByMovieId(Long movieId);
    List<Director> getDirectorsByMovieId(Long movieId);
    
    // DTO methods
    List<ActorDTO> getAllActorDTOs();
    List<DirectorDTO> getAllDirectorDTOs();
    ActorDTO getActorDTOById(Long id);
    DirectorDTO getDirectorDTOById(Long id);
    List<ActorDTO> getActorDTOsByMovieId(Long movieId);
    List<DirectorDTO> getDirectorDTOsByMovieId(Long movieId);
} 