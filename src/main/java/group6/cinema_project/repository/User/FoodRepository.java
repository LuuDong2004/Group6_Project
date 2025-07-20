package group6.cinema_project.repository.User;

import group6.cinema_project.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food,Integer> {
    Optional<Food> findByName(String name);
    // List<Food> findByNameContaining(String name);

}
