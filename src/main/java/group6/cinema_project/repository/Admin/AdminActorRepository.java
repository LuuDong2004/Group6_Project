package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminActorRepository extends JpaRepository<Actor, Integer> {

    /**
     * Tìm diễn viên theo tên
     *
     * @param name Tên diễn viên
     * @return Diễn viên tìm được
     */
    Actor findByName(String name);

    /**
     * Tìm diễn viên đầu tiên theo tên để xử lý trường hợp trùng lặp
     *
     * @param name Tên diễn viên
     * @return Optional chứa diễn viên đầu tiên tìm được
     */
    Optional<Actor> findFirstByName(String name);

}
