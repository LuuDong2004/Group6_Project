package group6.cinema_project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.Food;


@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    // Add custom query methods here if needed, e.g.:
    List<Food> findByName(String name);
    // List<Food> findByNameContaining(String name);
    
    // Thêm các phương thức phân trang, tìm kiếm và sắp xếp
    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Food> searchByName(@Param("name") String name, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT f FROM Food f ORDER BY f.price ASC")
    Page<Food> findAllOrderByPriceAsc(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT f FROM Food f ORDER BY f.price DESC")
    Page<Food> findAllOrderByPriceDesc(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY f.price ASC")
    Page<Food> searchByNameOrderByPriceAsc(@Param("name") String name, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY f.price DESC")
    Page<Food> searchByNameOrderByPriceDesc(@Param("name") String name, org.springframework.data.domain.Pageable pageable);
}