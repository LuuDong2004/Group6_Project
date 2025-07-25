
package group6.cinema_project.repository.User;


import group6.cinema_project.entity.Qa.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT d FROM Voucher d WHERE UPPER(d.code) = UPPER(:code) AND d.expiryDate >= :today AND d.status = 'ACTIVE'")
    Optional<Voucher> findValidCode(@Param("code") String code, @Param("today") LocalDate today);

    @Query("SELECT d FROM Voucher d WHERE (:id IS NULL OR d.id = :id) AND (:expiryDate IS NULL OR d.expiryDate = :expiryDate)")
    List<Voucher> findByIdAndExpiryDate(@Param("id") Long id, @Param("expiryDate") LocalDate expiryDate);
}
