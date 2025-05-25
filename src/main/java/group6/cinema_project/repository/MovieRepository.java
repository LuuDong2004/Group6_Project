package group6.cinema_project.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import group6.cinema_project.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer>{

}
