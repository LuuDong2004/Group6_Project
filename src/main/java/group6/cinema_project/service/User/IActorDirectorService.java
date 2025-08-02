package group6.cinema_project.service.User;

import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;

import java.util.List;

public interface IActorDirectorService {
    List<Actor> getAllActors();
    List<Director> getAllDirectors();
    Actor getActorById(Integer id);
    Director getDirectorById(Integer id);
    List<Actor> getActorsByMovieId(Integer movieId);
    List<Director> getDirectorsByMovieId(Integer movieId);

    // DTO methods
    List<ActorDto> getAllActorDTOs();
    List<DirectorDto> getAllDirectorDTOs();
    ActorDto getActorDTOById(Integer id);
    DirectorDto getDirectorDTOById(Integer id);
    List<ActorDto> getActorDTOsByMovieId(Integer movieId);
    List<DirectorDto> getDirectorDTOsByMovieId(Integer movieId);
} 