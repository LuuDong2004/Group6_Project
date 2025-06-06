package group6.cinema_project.repository;

import group6.cinema_project.entity.BookingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingProductRepository extends JpaRepository<BookingProduct, Integer> {
} 