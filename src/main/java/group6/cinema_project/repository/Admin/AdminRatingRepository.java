package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRatingRepository extends JpaRepository<Rating, Integer> {

    /**
     * Tìm rating theo code
     *
     * @param code Code của rating (G, K, T13, T16, T18, C)
     * @return Rating tìm được
     */
    Optional<Rating> findByCode(String code);

    /**
     * Lấy tất cả rating sắp xếp theo code
     *
     * @return Danh sách rating sắp xếp theo code
     */
    @Query("SELECT r FROM Rating r ORDER BY r.code")
    List<Rating> findAllOrderByCode();

    /**
     * Kiểm tra xem rating có tồn tại theo code không
     *
     * @param code Code của rating
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByCode(String code);
}
