package group6.cinema_project.controller;

import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.service.ActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ActorDirectorController {
    @Autowired
    private ActorDirectorService actorDirectorService;

    @GetMapping("/actors")
    public String getAllActors(Model model) {
        List<ActorDto> actors = actorDirectorService.getAllActorDTOs();
        model.addAttribute("actors", actors);
        return "actors";
    }

    @GetMapping("/directors")
    public String getAllDirectors(Model model) {
        List<DirectorDto> directors = actorDirectorService.getAllDirectorDTOs();
        model.addAttribute("directors", directors);
        return "directors";
    }

    @GetMapping("/actors/{id}")
    public String getActorById(@PathVariable Long id, Model model) {
        ActorDto actor = actorDirectorService.getActorDTOById(id);
        if (actor == null) {
            return "redirect:/actors";
        }
        model.addAttribute("actor", actor);
        return "person_detail";
    }

    @GetMapping("/directors/{id}")
    public String getDirectorById(@PathVariable Long id, Model model) {
        DirectorDto director = actorDirectorService.getDirectorDTOById(id);
        if (director == null) {
            return "redirect:/directors";
        }
        model.addAttribute("director", director);
        return "person_detail";
    }

    @GetMapping("/movies/{movieId}/actors")
    public String getActorsByMovieId(@PathVariable Long movieId, Model model) {
        List<ActorDto> actors = actorDirectorService.getActorDTOsByMovieId(movieId);
        model.addAttribute("actors", actors);
        model.addAttribute("movieId", movieId);
        return "movie_actors";
    }

    @GetMapping("/movies/{movieId}/directors")
    public String getDirectorsByMovieId(@PathVariable Long movieId, Model model) {
        List<DirectorDto> directors = actorDirectorService.getDirectorDTOsByMovieId(movieId);
        model.addAttribute("directors", directors);
        model.addAttribute("movieId", movieId);
        return "movie_directors";
    }
}