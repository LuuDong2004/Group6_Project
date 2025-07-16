package group6.cinema_project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.ScreeningRoom;

@Repository
public interface ScreeningRoomRepository extends JpaRepository<ScreeningRoom, Integer> {

    boolean existsByName(String name);
    boolean existsByBranchId(int branchId);
    boolean existsByBranchIdAndName(int branchId, String name);
    boolean existsByBranchIdAndNameAndIdNot(int branchId, String name, int id);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, int id);
    boolean existsByBranchIdAndNameIgnoreCase(int branchId, String name);
    boolean existsByBranchIdAndNameIgnoreCaseAndIdNot(int branchId, String name, int id);
    Page<ScreeningRoom> findByBranchId(int branchId, Pageable pageable);
    // Thêm các phương thức custom nếu cần
} 