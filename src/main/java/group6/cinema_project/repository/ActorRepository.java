package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.cinema_project.entity.Actor;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {

    Actor findByName(String name);
    
    List<Actor> findByNameIn(List<String> names);
}
