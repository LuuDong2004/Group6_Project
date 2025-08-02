package group6.cinema_project.service;

import group6.cinema_project.dto.BlogPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho Blog Service.
 * Định nghĩa các phương thức để xử lý logic nghiệp vụ cho blog posts.
 */
public interface IBlogService {

    /**
     * Lấy tất cả blog posts, sắp xếp theo thời gian tạo giảm dần.
     *
     * @return Danh sách blog posts
     */
    List<BlogPostDto> getAllBlogPosts();

    /**
     * Lấy blog posts với phân trang.
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts
     */
    Page<BlogPostDto> getBlogPostsWithPagination(Pageable pageable);

    /**
     * Lấy blog post theo ID.
     *
     * @param id ID của blog post
     * @return Optional chứa blog post nếu tìm thấy
     */
    Optional<BlogPostDto> getBlogPostById(Long id);

    /**
     * Tìm blog posts theo tiêu đề.
     *
     * @param title Tiêu đề cần tìm
     * @return Danh sách blog posts có tiêu đề chứa từ khóa
     */
    List<BlogPostDto> findBlogPostsByTitle(String title);

    /**
     * Tìm blog posts theo tác giả.
     *
     * @param authorId ID của tác giả
     * @return Danh sách blog posts của tác giả
     */
    List<BlogPostDto> findBlogPostsByAuthor(int authorId);

    /**
     * Lấy top N blog posts mới nhất.
     *
     * @param limit Số lượng blog posts cần lấy
     * @return Danh sách blog posts mới nhất
     */
    List<BlogPostDto> getLatestBlogPosts(int limit);

    /**
     * Tìm blog posts theo từ khóa trong tiêu đề hoặc nội dung.
     *
     * @param keyword Từ khóa cần tìm
     * @return Danh sách blog posts chứa từ khóa
     */
    List<BlogPostDto> searchBlogPosts(String keyword);

    /**
     * Tìm blog posts theo từ khóa với phân trang.
     *
     * @param keyword  Từ khóa cần tìm
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts chứa từ khóa
     */
    Page<BlogPostDto> searchBlogPostsWithPagination(String keyword, Pageable pageable);

    /**
     * Đếm tổng số blog posts.
     *
     * @return Số lượng blog posts
     */
    Long getTotalBlogPostsCount();

    /**
     * Lấy blog posts liên quan (cùng tác giả, loại trừ blog post hiện tại).
     *
     * @param currentBlogId ID của blog post hiện tại
     * @param authorId      ID của tác giả
     * @param limit         Số lượng blog posts liên quan cần lấy
     * @return Danh sách blog posts liên quan
     */
    List<BlogPostDto> getRelatedBlogPosts(Long currentBlogId, int authorId, int limit);

    /**
     * Kiểm tra xem blog post có tồn tại không.
     *
     * @param id ID của blog post
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsById(Long id);
}