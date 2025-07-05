package group6.cinema_project.controller;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.dto.ActorDTO;
import group6.cinema_project.dto.DirectorDTO;
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
    public List<ActorDTO> getAllActors() { return actorDirectorService.getAllActorDTOs(); }

    @GetMapping("/directors")
    public List<DirectorDTO> getAllDirectors() { return actorDirectorService.getAllDirectorDTOs(); }

    @GetMapping("/actors/{id}")
    public ActorDTO getActorById(@PathVariable Long id) { return actorDirectorService.getActorDTOById(id); }

    @GetMapping("/directors/{id}")
    public DirectorDTO getDirectorById(@PathVariable Long id) { return actorDirectorService.getDirectorDTOById(id); }

    @GetMapping("/movies/{movieId}/actors")
    public List<ActorDTO> getActorsByMovieId(@PathVariable Long movieId) { return actorDirectorService.getActorDTOsByMovieId(movieId); }

    @GetMapping("/movies/{movieId}/directors")
    public List<DirectorDTO> getDirectorsByMovieId(@PathVariable Long movieId) { return actorDirectorService.getDirectorDTOsByMovieId(movieId); }
}