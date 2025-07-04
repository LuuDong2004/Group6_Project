package group6.cinema_project.service;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.entity.ActorMovie;
import group6.cinema_project.entity.DirectorMovie;
import group6.cinema_project.repository.MovieRepository;
import group6.cinema_project.repository.ActorMovieRepository;
import group6.cinema_project.repository.DirectorMovieRepository;
import group6.cinema_project.repository.ReviewRepository;
import group6.cinema_project.controller.MovieController.MovieDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ActorMovieRepository actorMovieRepository;
    @Autowired
    private DirectorMovieRepository directorMovieRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<MovieDetailDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(movie -> {
            MovieDetailDTO dto = new MovieDetailDTO();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public MovieDetailDTO getMovieDetail(Long id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) return null;
        MovieDetailDTO dto = new MovieDetailDTO();
        dto.id = movie.getId();
        dto.name = movie.getName();
        dto.image = movie.getImage();
        dto.duration = movie.getDuration();
        dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
        try { dto.rating = Double.parseDouble(movie.getRating()); } catch(Exception e) { dto.rating = 0; }
        dto.genre = movie.getGenre();
        dto.language = movie.getLanguage();
        dto.format = "3D";
        dto.trailer = movie.getTrailer();
        dto.summary = "";
        dto.directors = directorMovieRepository.findByMovie_Id(id)
            .stream().map(DirectorMovie::getDirector)
            .map(Director::getName).collect(Collectors.toList());
        dto.actors = actorMovieRepository.findByMovieId(id)
            .stream()
            .map(ActorMovie::getActor)
            .map(Actor::getName)
            .distinct()
            .collect(Collectors.toList());
        dto.directorsData = directorMovieRepository.findByMovie_Id(id)
            .stream().map(dm -> {
                MovieDetailDTO.PersonDTO p = new MovieDetailDTO.PersonDTO();
                p.id = dm.getDirector().getId();
                p.name = dm.getDirector().getName();
                return p;
            }).collect(Collectors.toList());
        dto.actorsData = actorMovieRepository.findByMovieId(id)
            .stream().map(am -> {
                MovieDetailDTO.PersonDTO p = new MovieDetailDTO.PersonDTO();
                p.id = am.getActor().getId();
                p.name = am.getActor().getName();
                return p;
            }).collect(Collectors.toList());
        dto.reviews = reviewRepository.findByMovieId(id).stream().map(r -> {
            MovieDetailDTO.ReviewDTO rv = new MovieDetailDTO.ReviewDTO();
            rv.user = r.getUserId() != null ? r.getUserId().toString() : null;
            rv.comment = r.getComment();
            rv.rating = r.getRating();
            rv.date = r.getDate() != null ? r.getDate().toString() : null;
            return rv;
        }).collect(Collectors.toList());
        return dto;
    }

    @Override
    public List<MovieDetailDTO> getFeaturedMovies() {
        List<Movie> movies = movieRepository.findAll();
        movies.sort(Comparator.comparing(Movie::getId));
        List<MovieDetailDTO> dtos = movies.stream().map(movie -> {
            MovieDetailDTO dto = new MovieDetailDTO();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        })
        .filter(dto -> dto.rating >= 0 && dto.rating <= 10)
        .sorted((a, b) -> Double.compare(b.rating, a.rating))
        .collect(Collectors.toList());
        return dtos.size() > 6 ? dtos.subList(0, 6) : dtos;
    }

    @Override
    public List<String> getAllGenres() {
        return movieRepository.findAllGenres();
    }

    @Override
    public List<MovieDetailDTO> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findByGenre(genre);
        return movies.stream().map(movie -> {
            MovieDetailDTO dto = new MovieDetailDTO();
            dto.id = movie.getId();
            dto.name = movie.getName();
            dto.image = movie.getImage();
            dto.duration = movie.getDuration();
            dto.release_date = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            Double avgRating = reviewRepository.findAverageRatingByMovieId(movie.getId());
            dto.rating = avgRating != null ? avgRating : 0;
            dto.genre = movie.getGenre();
            dto.language = movie.getLanguage();
            dto.format = "3D";
            dto.trailer = movie.getTrailer();
            dto.summary = "";
            return dto;
        }).collect(Collectors.toList());
    }
} 