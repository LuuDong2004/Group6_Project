package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.cinema_project.entity.Movie;
@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

}
