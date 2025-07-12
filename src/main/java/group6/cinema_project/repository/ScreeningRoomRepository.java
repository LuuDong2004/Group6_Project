package group6.cinema_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.ScreeningRoom;

import java.util.Optional;

@Repository
public interface ScreeningRoomRepository extends JpaRepository<ScreeningRoom, Integer> {

    // Thêm các phương thức custom nếu cần
    Optional<ScreeningRoom> findByName (String name);
    boolean existsByName(String name);
    boolean existsById(int id);

    ScreeningRoom findByNameAndBranchId(String name, int branchId);
    boolean existsByNameAndBranchId(String name, int branchId);
    boolean existsByIdAndBranchId(int id, int branchId);

}
