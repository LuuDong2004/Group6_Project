package group6.cinema_project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group6.cinema_project.entity.CinemaChain;

public interface CinemaChainRepository extends JpaRepository<CinemaChain, Integer> {
    List<CinemaChain> findByName(String name);

    @Query("SELECT c FROM CinemaChain c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CinemaChain> searchByName(@Param("name") String name, Pageable pageable);
} 