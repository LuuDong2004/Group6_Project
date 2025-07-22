package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDirectorRepository extends JpaRepository<Director, Integer> {

    /**
     * Tìm đạo diễn đầu tiên theo tên để xử lý trường hợp trùng lặp
     * 
     * @param name Tên đạo diễn
     * @return Optional chứa đạo diễn đầu tiên tìm được
     */
    Optional<Director> findFirstByName(String name);
}
