package group6.cinema_project.repository.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import group6.cinema_project.entity.BlogPost;

/**
 * Repository interface cho BlogPost entity.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu cho blog posts.
 */
@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long> {

    /**
     * Tìm tất cả blog posts được sắp xếp theo thời gian tạo giảm dần (mới nhất
     * trước).
     *
     * @return Danh sách blog posts
     */
    @Query("SELECT b FROM BlogPost b ORDER BY b.createdAt DESC")
    List<BlogPost> findAllOrderByCreatedAtDesc();

    /**
     * Tìm blog posts với phân trang, sắp xếp theo thời gian tạo giảm dần.
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts
     */
    @Query("SELECT b FROM BlogPost b ORDER BY b.createdAt DESC")
    Page<BlogPost> findAllOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Tìm blog posts theo tiêu đề (không phân biệt hoa thường).
     *
     * @param title Tiêu đề cần tìm
     * @return Danh sách blog posts có tiêu đề chứa từ khóa
     */
    @Query("SELECT b FROM BlogPost b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY b.createdAt DESC")
    List<BlogPost> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Tìm blog posts theo tác giả.
     *
     * @param authorId ID của tác giả
     * @return Danh sách blog posts của tác giả
     */
    @Query("SELECT b FROM BlogPost b WHERE b.author.id = :authorId ORDER BY b.createdAt DESC")
    List<BlogPost> findByAuthorId(@Param("authorId") int authorId);

    /**
     * Lấy top N blog posts mới nhất.
     *
     * @param pageable Thông tin phân trang (chỉ cần limit)
     * @return Danh sách blog posts mới nhất
     */
    @Query("SELECT b FROM BlogPost b ORDER BY b.createdAt DESC")
    List<BlogPost> findTopBlogPosts(Pageable pageable);

    /**
     * Đếm tổng số blog posts.
     *
     * @return Số lượng blog posts
     */
    @Query("SELECT COUNT(b) FROM BlogPost b")
    Long countAllBlogPosts();

    /**
     * Tìm blog posts theo từ khóa trong tiêu đề hoặc nội dung.
     *
     * @param keyword Từ khóa cần tìm
     * @return Danh sách blog posts chứa từ khóa
     */
    @Query("SELECT b FROM BlogPost b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "b.content LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY b.createdAt DESC")
    List<BlogPost> findByKeyword(@Param("keyword") String keyword);

    /**
     * Tìm blog posts theo từ khóa với phân trang.
     *
     * @param keyword  Từ khóa cần tìm
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts chứa từ khóa
     */
    @Query("SELECT b FROM BlogPost b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "b.content LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY b.createdAt DESC")
    Page<BlogPost> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
