package group6.cinema_project.controller.User;

import group6.cinema_project.dto.DirectorDto;
import group6.cinema_project.service.User.IActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/directors")
public class DirectorApiController {
    @Autowired
    private IActorDirectorService actorDirectorService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDirectorById(@PathVariable Integer id) {
        DirectorDto director = actorDirectorService.getDirectorDTOById(id);
        if (director == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(director);
    }
}
