package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.User.ActorRepository;
import group6.cinema_project.repository.User.DirectorRepository;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorDirectorServiceImpl implements IActorDirectorService {
    @Autowired private ActorRepository actorRepository;
    @Autowired private DirectorRepository directorRepository;
    @Autowired private MovieRepository movieRepository;

    @Override
    public List<Actor> getAllActors() { return actorRepository.findAll(); }
    @Override
    public List<Director> getAllDirectors() { return directorRepository.findAll(); }
    @Override
    public Actor getActorById(Integer id) { return actorRepository.findById(id).orElse(null); }
    @Override
    public Director getDirectorById(Integer id) { return directorRepository.findById(id).orElse(null); }
    @Override
    public List<Actor> getActorsByMovieId(Integer movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie != null && movie.getActors() != null && !movie.getActors().isEmpty()) {
            return new ArrayList<>(movie.getActors());
        }
        return List.of();
    }

    @Override
    public List<Director> getDirectorsByMovieId(Integer movieId) {
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
    public ActorDto getActorDTOById(Integer id) {
        Actor actor = actorRepository.findById(id).orElse(null);
        return actor != null ? convertToActorDTO(actor) : null;
    }

    @Override
    public DirectorDto getDirectorDTOById(Integer id) {
        Director director = directorRepository.findById(id).orElse(null);
        return director != null ? convertToDirectorDTO(director) : null;
    }

    @Override
    public List<ActorDto> getActorDTOsByMovieId(Integer movieId) {
        return getActorsByMovieId(movieId).stream().map(this::convertToActorDTO).collect(Collectors.toList());
    }

    @Override
    public List<DirectorDto> getDirectorDTOsByMovieId(Integer movieId) {
        // Nếu không còn liên kết nhiều-nhiều, trả về rỗng hoặc lấy theo quan hệ mới nếu có
        return List.of();
    }

    private ActorDto convertToActorDTO(Actor actor) {
        ActorDto dto = new ActorDto();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setImageUrl(actor.getImageUrl());
        dto.setDescription(actor.getDescription());
        return dto;
    }

    private DirectorDto convertToDirectorDTO(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        dto.setImageUrl(director.getImageUrl());
        dto.setDescription(director.getDescription());
        return dto;
    }
} 