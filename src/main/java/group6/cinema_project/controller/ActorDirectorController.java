package group6.cinema_project.controller;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.service.ActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActorDirectorController {
    @Autowired
    private ActorDirectorService actorDirectorService;

    @GetMapping("/actors")
    public List<Actor> getAllActors() { return actorDirectorService.getAllActors(); }

    @GetMapping("/directors")
    public List<Director> getAllDirectors() { return actorDirectorService.getAllDirectors(); }

    @GetMapping("/actors/{id}")
    public Actor getActorById(@PathVariable Long id) { return actorDirectorService.getActorById(id); }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable Long id) { return actorDirectorService.getDirectorById(id); }

    @GetMapping("/movies/{movieId}/actors")
    public List<Actor> getActorsByMovieId(@PathVariable Long movieId) { return actorDirectorService.getActorsByMovieId(movieId); }

    @GetMapping("/movies/{movieId}/directors")
    public List<Director> getDirectorsByMovieId(@PathVariable Long movieId) { return actorDirectorService.getDirectorsByMovieId(movieId); }
}