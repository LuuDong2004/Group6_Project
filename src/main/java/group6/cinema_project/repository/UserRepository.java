package group6.cinema_project.repository;

import group6.cinema_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Tìm user theo username.
     *
     * @param username Username cần tìm
     * @return Optional chứa User nếu tìm thấy
     */
    Optional<User> findByUsername(String username);
}
