package group6.cinema_project.repository;

import group6.cinema_project.entity.ActorMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorMovieRepository extends JpaRepository<ActorMovie, Long> {
    List<ActorMovie> findByMovieId(Long movieId);
}