package group6.cinema_project.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<ScreeningRoom> findByName(String name);
    @Query("SELECT r FROM ScreeningRoom r WHERE r.branch.id = :branchId"
        + " AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))"
        + " AND (:type IS NULL OR r.type = :type)"
        + " AND (:status IS NULL OR r.status = :status)"
        + " AND (:rows IS NULL OR r.rows = :rows)"
        + " AND (:seatsPerRow IS NULL OR r.seatsPerRow = :seatsPerRow)")
    Page<ScreeningRoom> searchRooms(
        @Param("branchId") int branchId,
        @Param("name") String name,
        @Param("type") String type,
        @Param("status") String status,
        @Param("rows") Integer rows,
        @Param("seatsPerRow") Integer seatsPerRow,
        Pageable pageable);
    // Thêm các phương thức custom nếu cần
} 