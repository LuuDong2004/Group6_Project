package group6.cinema_project.repository;

import group6.cinema_project.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    @Query("SELECT d FROM DiscountCode d WHERE UPPER(d.code) = UPPER(:code) AND d.expiryDate >= :today")
    Optional<DiscountCode> findValidCode(@Param("code") String code, @Param("today") LocalDate today);

    @Query("SELECT d FROM DiscountCode d WHERE (:id IS NULL OR d.id = :id) AND (:expiryDate IS NULL OR d.expiryDate = :expiryDate)")
    List<DiscountCode> findByIdAndExpiryDate(@Param("id") Long id, @Param("expiryDate") LocalDate expiryDate);
}
