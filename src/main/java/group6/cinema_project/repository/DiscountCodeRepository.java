package group6.cinema_project.repository;

import group6.cinema_project.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    @Query("SELECT d FROM DiscountCode d WHERE UPPER(d.code) = UPPER(:code) AND d.expiryDate >= :today")
    Optional<DiscountCode> findValidCode(@Param("code") String code, @Param("today") LocalDate today);
} 