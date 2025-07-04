package group6.cinema_project.service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import java.util.List;

public interface ActorDirectorService {
    List<Actor> getAllActors();
    List<Director> getAllDirectors();
    Actor getActorById(Long id);
    Director getDirectorById(Long id);
    List<Actor> getActorsByMovieId(Long movieId);
    List<Director> getDirectorsByMovieId(Long movieId);
} 