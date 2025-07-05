package group6.cinema_project.controller;

import group6.cinema_project.entity.User;
import group6.cinema_project.dto.UserDTO;
import group6.cinema_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserDTOById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
} 