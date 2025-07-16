package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminDirectorRepository extends JpaRepository<Director, Integer> {
    Director findByName(String name);

    // Add method to find first director by name to handle duplicates
    Optional<Director> findFirstByName(String name);
}
