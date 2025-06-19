package group6.cinema_project.repository;

import group6.cinema_project.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    // Add custom query methods here if needed, e.g.:
    Optional<Food> findByName(String name);
    // List<Food> findByNameContaining(String name);
}