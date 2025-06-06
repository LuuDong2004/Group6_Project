package group6.cinema_project.repository;

import group6.cinema_project.entity.MoviePerson;
import group6.cinema_project.entity.MoviePersonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviePersonRepository extends JpaRepository<MoviePerson, MoviePersonId> {
} 