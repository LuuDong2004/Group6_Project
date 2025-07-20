
package group6.cinema_project.repository.User;

import java.util.Optional;

import group6.cinema_project.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.user.email = :email")
    void deleteByUserEmail(@Param("email") String email);
}
