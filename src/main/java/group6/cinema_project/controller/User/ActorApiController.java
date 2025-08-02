package group6.cinema_project.controller.User;

import group6.cinema_project.dto.ActorDto;
import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.service.User.IActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
public class ActorApiController {
    @Autowired
    private IActorDirectorService actorDirectorService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getActorById(@PathVariable Integer id) {
        ActorDto actor = actorDirectorService.getActorDTOById(id);
        if (actor == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actor);
    }
}

