package group6.cinema_project.controller;

import group6.cinema_project.entity.*;
import group6.cinema_project.repository.ActorRepository;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.DirectorMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActorDirectorController {
    // Rest of the code remains the same
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private ActorMovieRepository actorMovieRepository;
    @Autowired
    private DirectorMovieRepository directorMovieRepository;

    @GetMapping("/actors")
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    @GetMapping("/directors")
    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    @GetMapping("/actors/{id}")
    public Actor getActorById(@PathVariable Long id) {
        return actorRepository.findById(id).orElse(null);
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable Long id) {
        return directorRepository.findById(id).orElse(null);
    }

    @GetMapping("/movies/{movieId}/actors")
    public List<Actor> getActorsByMovieId(@PathVariable Long movieId) {
        return actorMovieRepository.findByMovieId(movieId)
                .stream().map(ActorMovie::getActor).collect(Collectors.toList());
    }

    @GetMapping("/movies/{movieId}/directors")
    public List<Director> getDirectorsByMovieId(@PathVariable Long movieId) {
        return directorMovieRepository.findByMovie_Id(movieId)
                .stream().map(DirectorMovie::getDirector).collect(Collectors.toList());
    }
}