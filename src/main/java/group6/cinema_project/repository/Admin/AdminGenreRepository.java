package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminGenreRepository extends JpaRepository<Genre, Integer> {

    /**
     * Tìm genre theo tên
     *
     * @param name Tên genre
     * @return Genre tìm được
     */
    Optional<Genre> findByName(String name);

    /**
     * Lấy tất cả genre sắp xếp theo tên
     *
     * @return Danh sách genre sắp xếp theo tên
     */
    @Query("SELECT g FROM Genre g ORDER BY g.name")
    List<Genre> findAllOrderByName();

    /**
     * Kiểm tra xem genre có tồn tại theo tên không
     *
     * @param name Tên genre
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByName(String name);

    /**
     * Tìm genre theo danh sách ID
     *
     * @param ids Danh sách ID của genre
     * @return Danh sách genre tương ứng
     */
    @Query("SELECT g FROM Genre g WHERE g.id IN :ids")
    List<Genre> findByIdIn(@Param("ids") List<Integer> ids);
}
