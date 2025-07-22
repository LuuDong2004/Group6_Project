package group6.cinema_project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group6.cinema_project.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
    List<Branch> findByCinemaChainId(int cinemaChainId);
    List<Branch> findByName(String name);
    @Query("SELECT b FROM Branch b WHERE (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:address IS NULL OR LOWER(b.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND (:cinemaChain IS NULL OR LOWER(b.cinemaChain.name) LIKE LOWER(CONCAT('%', :cinemaChain, '%')))")
    Page<group6.cinema_project.entity.Branch> searchBranches(@Param("name") String name, @Param("address") String address, @Param("cinemaChain") String cinemaChain, Pageable pageable);
} 
