package group6.cinema_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import group6.cinema_project.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    
    // Kiểm tra xem đã có khu vực với cùng tên và miền chưa
    @Query("SELECT r FROM Region r WHERE r.name = :name AND r.type = :type")
    Optional<Region> findByNameAndType(@Param("name") String name, @Param("type") String type);
    
    // Kiểm tra xem đã có khu vực với cùng tên và miền (trừ ID hiện tại khi edit)
    @Query("SELECT r FROM Region r WHERE r.name = :name AND r.type = :type AND r.id != :id")
    Optional<Region> findByNameAndTypeExcludingId(@Param("name") String name, @Param("type") String type, @Param("id") int id);
    
    // Kiểm tra xem đã có khu vực với cùng tên chưa
    boolean existsByName(String name);
} 