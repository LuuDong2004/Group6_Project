package group6.cinema_project.service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.entity.DirectorMovie;
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
} 