package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.ScreeningRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoomRepository extends JpaRepository<ScreeningRoom,Integer> {
    List<ScreeningRoom> findByBranchId(Integer branchId);

    List<ScreeningRoom> findByNameContainingIgnoreCase(String name);
}
