package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.Director;

import java.util.List;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Integer> {
    Director findByName(String name);

    List<Director> findByNameIn(List<String> names);
    
}
