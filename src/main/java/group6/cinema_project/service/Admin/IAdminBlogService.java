package group6.cinema_project.service.Admin;

import group6.cinema_project.dto.BlogPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Interface cho Admin Blog Service.
 * Định nghĩa các phương thức để admin quản lý blog posts.
 */
public interface IAdminBlogService {

    /**
     * Lấy tất cả blog posts với thông tin tác giả cho admin.
     *
     * @return Danh sách blog posts với thông tin tác giả
     */
    List<BlogPostDto> getAllBlogPostsForAdmin();

    /**
     * Lấy blog posts với phân trang cho admin.
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts với thông tin tác giả
     */
    Page<BlogPostDto> getBlogPostsWithPaginationForAdmin(Pageable pageable);

    /**
     * Lấy blog post theo ID với thông tin tác giả cho admin.
     *
     * @param id ID của blog post
     * @return Optional chứa blog post nếu tìm thấy
     */
    Optional<BlogPostDto> getBlogPostByIdForAdmin(Long id);

    /**
     * Tạo blog post mới.
     *
     * @param blogPostDto Thông tin blog post
     * @return Blog post đã được tạo
     */
    BlogPostDto createBlogPost(BlogPostDto blogPostDto);

    /**
     * Cập nhật blog post.
     *
     * @param id          ID của blog post cần cập nhật
     * @param blogPostDto Thông tin blog post mới
     * @return Blog post đã được cập nhật
     */
    BlogPostDto updateBlogPost(Long id, BlogPostDto blogPostDto);

    /**
     * Xóa blog post.
     *
     * @param id ID của blog post cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    boolean deleteBlogPost(Long id);

    /**
     * Tìm blog posts theo tiêu đề cho admin.
     *
     * @param title Tiêu đề cần tìm
     * @return Danh sách blog posts có tiêu đề chứa từ khóa
     */
    List<BlogPostDto> findBlogPostsByTitleForAdmin(String title);

    /**
     * Tìm blog posts theo tác giả cho admin.
     *
     * @param authorId ID của tác giả
     * @return Danh sách blog posts của tác giả
     */
    List<BlogPostDto> findBlogPostsByAuthorForAdmin(int authorId);

    /**
     * Tìm blog posts theo từ khóa cho admin.
     *
     * @param keyword Từ khóa cần tìm
     * @return Danh sách blog posts chứa từ khóa
     */
    List<BlogPostDto> searchBlogPostsForAdmin(String keyword);

    /**
     * Tìm blog posts theo từ khóa với phân trang cho admin.
     *
     * @param keyword  Từ khóa cần tìm
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts chứa từ khóa
     */
    Page<BlogPostDto> searchBlogPostsWithPaginationForAdmin(String keyword, Pageable pageable);

    /**
     * Upload ảnh bìa cho blog post.
     *
     * @param imageFile File ảnh cần upload
     * @return URL của ảnh đã upload
     * @throws IOException Nếu có lỗi khi upload file
     */
    String uploadCoverImage(MultipartFile imageFile) throws IOException;

    /**
     * Xóa ảnh bìa.
     *
     * @param imageUrl URL của ảnh cần xóa
     * @return true nếu xóa thành công, false nếu không
     */
    boolean deleteCoverImage(String imageUrl);

    /**
     * Kiểm tra xem tiêu đề blog post có bị trùng lặp không.
     *
     * @param title Tiêu đề cần kiểm tra
     * @return true nếu trùng lặp, false nếu không
     */
    boolean isTitleDuplicate(String title);

    /**
     * Kiểm tra xem tiêu đề blog post có bị trùng lặp không (cho update).
     *
     * @param title     Tiêu đề cần kiểm tra
     * @param currentId ID của blog post hiện tại (để loại trừ)
     * @return true nếu trùng lặp, false nếu không
     */
    boolean isTitleDuplicateForUpdate(String title, Long currentId);

    /**
     * Đếm blog posts theo tác giả.
     *
     * @param authorId ID của tác giả
     * @return Số lượng blog posts của tác giả
     */
    Long countBlogPostsByAuthor(int authorId);

    /**
     * Validate blog post data.
     *
     * @param blogPostDto Blog post cần validate
     * @return Danh sách lỗi validation (rỗng nếu hợp lệ)
     */
    List<String> validateBlogPost(BlogPostDto blogPostDto);

    /**
     * Tạo blog post với ảnh bìa.
     *
     * @param blogPostDto    Thông tin blog post
     * @param coverImageFile File ảnh bìa
     * @return Blog post đã được tạo
     * @throws IOException Nếu có lỗi khi upload ảnh
     */
    BlogPostDto createBlogPostWithImage(BlogPostDto blogPostDto, MultipartFile coverImageFile) throws IOException;

    /**
     * Cập nhật blog post với ảnh bìa mới.
     *
     * @param id             ID của blog post cần cập nhật
     * @param blogPostDto    Thông tin blog post mới
     * @param coverImageFile File ảnh bìa mới (có thể null)
     * @return Blog post đã được cập nhật
     * @throws IOException Nếu có lỗi khi upload ảnh
     */
    BlogPostDto updateBlogPostWithImage(Long id, BlogPostDto blogPostDto, MultipartFile coverImageFile)
            throws IOException;
}