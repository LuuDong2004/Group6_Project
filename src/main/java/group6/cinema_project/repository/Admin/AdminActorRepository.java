package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface AdminActorRepository extends JpaRepository<Actor,Integer> {

    Actor findByName(String name);

    // Add method to find first actor by name to handle duplicates
    Optional<Actor> findFirstByName(String name);

}
