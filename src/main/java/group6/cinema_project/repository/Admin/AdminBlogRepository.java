package group6.cinema_project.repository.Admin;

import group6.cinema_project.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho Admin quản lý BlogPost.
 * Cung cấp các phương thức truy vấn cơ sở dữ liệu cho admin quản lý blog posts.
 */
@Repository
public interface AdminBlogRepository extends JpaRepository<BlogPost, Long> {

       /**
        * Tìm tất cả blog posts với thông tin tác giả, sắp xếp theo ID tăng dần.
        *
        * @return Danh sách blog posts với thông tin tác giả
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author ORDER BY b.id ASC")
       List<BlogPost> findAllWithAuthorOrderByIdAsc();

       /**
        * Tìm blog posts với phân trang và thông tin tác giả, sắp xếp theo ID tăng dần.
        *
        * @param pageable Thông tin phân trang
        * @return Page chứa blog posts với thông tin tác giả
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author ORDER BY b.id ASC")
       Page<BlogPost> findAllWithAuthorOrderByIdAsc(Pageable pageable);

       /**
        * Tìm blog post theo ID với thông tin tác giả.
        * 
        * @param id ID của blog post
        * @return Optional chứa blog post với thông tin tác giả
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author WHERE b.id = :id")
       Optional<BlogPost> findByIdWithAuthor(@Param("id") Long id);

       /**
        * Tìm blog posts theo tiêu đề cho admin (không phân biệt hoa thường), sắp xếp
        * theo ID tăng dần.
        *
        * @param title Tiêu đề cần tìm
        * @return Danh sách blog posts có tiêu đề chứa từ khóa
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author " +
                     "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
                     "ORDER BY b.id ASC")
       List<BlogPost> findByTitleContainingIgnoreCaseWithAuthor(@Param("title") String title);

       /**
        * Tìm blog posts theo tác giả cho admin, sắp xếp theo ID tăng dần.
        *
        * @param authorId ID của tác giả
        * @return Danh sách blog posts của tác giả
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author " +
                     "WHERE b.author.id = :authorId " +
                     "ORDER BY b.id ASC")
       List<BlogPost> findByAuthorIdWithAuthor(@Param("authorId") int authorId);

       /**
        * Tìm blog posts theo từ khóa trong tiêu đề hoặc nội dung cho admin, sắp xếp
        * theo ID tăng dần.
        *
        * @param keyword Từ khóa cần tìm
        * @return Danh sách blog posts chứa từ khóa
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author WHERE " +
                     "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "b.content LIKE CONCAT('%', :keyword, '%') " +
                     "ORDER BY b.id ASC")
       List<BlogPost> findByKeywordWithAuthor(@Param("keyword") String keyword);

       /**
        * Tìm blog posts theo từ khóa với phân trang cho admin, sắp xếp theo ID tăng
        * dần.
        *
        * @param keyword  Từ khóa cần tìm
        * @param pageable Thông tin phân trang
        * @return Page chứa blog posts chứa từ khóa
        */
       @Query("SELECT b FROM BlogPost b LEFT JOIN FETCH b.author WHERE " +
                     "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "b.content LIKE CONCAT('%', :keyword, '%') " +
                     "ORDER BY b.id ASC")
       Page<BlogPost> findByKeywordWithAuthor(@Param("keyword") String keyword, Pageable pageable);

       /**
        * Đếm blog posts theo tác giả.
        *
        * @param authorId ID của tác giả
        * @return Số lượng blog posts của tác giả
        */
       @Query("SELECT COUNT(b) FROM BlogPost b WHERE b.author.id = :authorId")
       Long countByAuthorId(@Param("authorId") int authorId);

       /**
        * Kiểm tra xem blog post có tồn tại theo tiêu đề không (để tránh trùng lặp).
        * 
        * @param title Tiêu đề cần kiểm tra
        * @return true nếu tồn tại, false nếu không
        */
       @Query("SELECT COUNT(b) > 0 FROM BlogPost b WHERE LOWER(b.title) = LOWER(:title)")
       boolean existsByTitleIgnoreCase(@Param("title") String title);

       /**
        * Kiểm tra xem blog post có tồn tại theo tiêu đề nhưng khác ID (cho update).
        * 
        * @param title Tiêu đề cần kiểm tra
        * @param id    ID của blog post hiện tại (để loại trừ)
        * @return true nếu tồn tại, false nếu không
        */
       @Query("SELECT COUNT(b) > 0 FROM BlogPost b WHERE LOWER(b.title) = LOWER(:title) AND b.id != :id")
       boolean existsByTitleIgnoreCaseAndIdNot(@Param("title") String title, @Param("id") Long id);
}
