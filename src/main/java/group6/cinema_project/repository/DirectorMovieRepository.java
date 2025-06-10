package group6.cinema_project.repository;

import group6.cinema_project.entity.DirectorMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorMovieRepository extends JpaRepository<DirectorMovie, Long> {
    List<DirectorMovie> findByMovie_Id(Long movieId);
}