package group6.cinema_project.repository;

import group6.cinema_project.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    
    List<Branch> findByNameContainingIgnoreCase(String name); 

}
