package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group6.cinema_project.entity.CinemaChain;

public interface CinemaChainRepository extends JpaRepository<CinemaChain, Integer> {
} 