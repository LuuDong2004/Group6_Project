package group6.cinema_project.controller;

import group6.cinema_project.dto.CinemaDto;
import group6.cinema_project.entity.Cinema;
import group6.cinema_project.repository.CinemaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cinema/nearest")
public class NearestCinemaController {
    private final CinemaRepository cinemaRepository = new CinemaRepository();

    @GetMapping("/search")
    public List<CinemaDto> getNearestCinemas(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") int limit) {
        List<Cinema> cinemas = cinemaRepository.findAll();
        return cinemas.stream()
            .sorted(Comparator.comparingDouble(
                c -> c.distanceTo(lat, lng)))
            .limit(limit)
            .map(cinema -> {
                CinemaDto dto = new CinemaDto();
                dto.setName(cinema.getName());
                dto.setLatitude(cinema.getLat());
                dto.setLongitude(cinema.getLng());
                dto.setAddress(""); // Nếu có trường address thì lấy, tạm để rỗng
                dto.setGoogleMapsUrl("https://www.google.com/maps/search/?api=1&query="
                    + cinema.getLat() + "," + cinema.getLng());
                return dto;
            })
            .collect(Collectors.toList());
    }
}
