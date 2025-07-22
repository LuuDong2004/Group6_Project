package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.ScreeningRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoomRepository extends JpaRepository<ScreeningRoom, Integer> {

    /**
     * Tìm phòng chiếu theo ID chi nhánh
     * 
     * @param branchId ID của chi nhánh
     * @return Danh sách phòng chiếu thuộc chi nhánh
     */
    List<ScreeningRoom> findByBranchId(Integer branchId);

    /**
     * Tìm phòng chiếu theo tên (không phân biệt hoa thường)
     * 
     * @param name Tên phòng chiếu cần tìm
     * @return Danh sách phòng chiếu có tên chứa từ khóa tìm kiếm
     */
    List<ScreeningRoom> findByNameContainingIgnoreCase(String name);
}
