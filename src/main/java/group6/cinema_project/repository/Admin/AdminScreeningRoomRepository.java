package group6.cinema_project.repository.Admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.ScreeningRoom;

@Repository
public interface AdminScreeningRoomRepository extends JpaRepository<ScreeningRoom, Integer> {
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

    // Kiểm tra xem phòng chiếu có suất chiếu đang hoạt động hoặc sắp chiếu không
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM ScreeningSchedule ss " +
           "WHERE ss.screening_room_id = :roomId " +
           "AND (ss.status = 'ACTIVE' OR ss.status = 'UPCOMING') " +
           "AND (ss.screening_date > :currentDate " +
           "OR (ss.screening_date = :currentDate AND CAST(ss.end_time AS TIME) > CAST(:currentTime AS TIME)))", 
           nativeQuery = true)
    Integer hasActiveSchedules(@Param("roomId") Integer roomId, 
                              @Param("currentDate") Date currentDate, 
                              @Param("currentTime") java.sql.Time currentTime);
    
    // Lấy danh sách suất chiếu đang hoạt động hoặc sắp chiếu của phòng
    @Query(value = "SELECT * FROM ScreeningSchedule ss " +
           "WHERE ss.screening_room_id = :roomId " +
           "AND (ss.status = 'ACTIVE' OR ss.status = 'UPCOMING') " +
           "AND (ss.screening_date > :currentDate " +
           "OR (ss.screening_date = :currentDate AND CAST(ss.end_time AS TIME) > CAST(:currentTime AS TIME))) " +
           "ORDER BY ss.screening_date ASC, ss.start_time ASC", 
           nativeQuery = true)
    List<group6.cinema_project.entity.ScreeningSchedule> getActiveSchedules(@Param("roomId") Integer roomId, 
                                                                           @Param("currentDate") Date currentDate, 
                                                                           @Param("currentTime") java.sql.Time currentTime);
}
