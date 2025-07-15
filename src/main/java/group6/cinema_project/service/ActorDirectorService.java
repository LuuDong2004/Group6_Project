package group6.cinema_project.service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import java.util.List;

public interface ActorDirectorService {
    List<Actor> getAllActors();
    List<Director> getAllDirectors();
    Actor getActorById(Long id);
    Director getDirectorById(Long id);
    List<Actor> getActorsByMovieId(Long movieId);
    List<Director> getDirectorsByMovieId(Long movieId);
    
    // DTO methods
    List<ActorDto> getAllActorDTOs();
    List<DirectorDto> getAllDirectorDTOs();
    ActorDto getActorDTOById(Long id);
    DirectorDto getDirectorDTOById(Long id);
    List<ActorDto> getActorDTOsByMovieId(Long movieId);
    List<DirectorDto> getDirectorDTOsByMovieId(Long movieId);
} 