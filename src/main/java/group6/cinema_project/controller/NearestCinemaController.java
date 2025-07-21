package group6.cinema_project.controller;

import group6.cinema_project.entity.Cinema;
import group6.cinema_project.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cinemas")
public class NearestCinemaController {
    @Autowired
    private CinemaRepository cinemaRepository;

    @GetMapping("/nearest")
    public List<Cinema> getNearestCinemas(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") int limit) {
        List<Cinema> cinemas = cinemaRepository.findAll();
        return cinemas.stream()
            .sorted(Comparator.comparingDouble(
                c -> c.distanceTo(lat, lng)))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }
}
