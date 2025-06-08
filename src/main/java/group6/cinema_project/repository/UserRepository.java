package group6.cinema_project.repository;

import group6.cinema_project.entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    Optional<User> findByUserNameOrEmail(String userName, String email, Limit limit);

    Optional<User> findByPhone(String phone);
}
