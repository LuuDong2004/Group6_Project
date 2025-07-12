package group6.cinema_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import group6.cinema_project.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
    List<Branch> findByCinemaChainId(int cinemaChainId);
} 