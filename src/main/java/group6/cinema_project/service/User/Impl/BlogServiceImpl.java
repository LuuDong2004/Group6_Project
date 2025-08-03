
package group6.cinema_project.service.User.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.cinema_project.dto.BlogPostDto;
import group6.cinema_project.entity.BlogPost;
import group6.cinema_project.repository.User.BlogRepository;
import group6.cinema_project.service.User.IBlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của IBlogService.
 * Xử lý logic nghiệp vụ cho blog posts.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BlogServiceImpl implements IBlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> getAllBlogPosts() {
        log.info("Lấy tất cả blog posts");
        try {
            List<BlogPost> blogPosts = blogRepository.findAllOrderByCreatedAtDesc();
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả blog posts", e);
            throw new RuntimeException("Không thể lấy danh sách blog posts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostDto> getBlogPostsWithPagination(Pageable pageable) {
        log.info("Lấy blog posts với phân trang: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BlogPost> blogPostPage = blogRepository.findAllOrderByCreatedAtDesc(pageable);
            return blogPostPage.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi lấy blog posts với phân trang", e);
            throw new RuntimeException("Không thể lấy danh sách blog posts với phân trang", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BlogPostDto> getBlogPostById(Long id) {
        log.info("Lấy blog post theo ID: {}", id);
        try {
            Optional<BlogPost> blogPost = blogRepository.findById(id);
            return blogPost.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi lấy blog post theo ID: {}", id, e);
            throw new RuntimeException("Không thể lấy blog post với ID: " + id, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> findBlogPostsByTitle(String title) {
        log.info("Tìm blog posts theo tiêu đề: {}", title);
        try {
            List<BlogPost> blogPosts = blogRepository.findByTitleContainingIgnoreCase(title);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi tìm blog posts theo tiêu đề: {}", title, e);
            throw new RuntimeException("Không thể tìm blog posts theo tiêu đề", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> findBlogPostsByAuthor(int authorId) {
        log.info("Tìm blog posts theo tác giả: {}", authorId);
        try {
            List<BlogPost> blogPosts = blogRepository.findByAuthorId(authorId);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi tìm blog posts theo tác giả: {}", authorId, e);
            throw new RuntimeException("Không thể tìm blog posts theo tác giả", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> getLatestBlogPosts(int limit) {
        log.info("Lấy {} blog posts mới nhất", limit);
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<BlogPost> blogPosts = blogRepository.findTopBlogPosts(pageable);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy {} blog posts mới nhất", limit, e);
            throw new RuntimeException("Không thể lấy blog posts mới nhất", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> searchBlogPosts(String keyword) {
        log.info("Tìm kiếm blog posts với từ khóa: {}", keyword);
        try {
            List<BlogPost> blogPosts = blogRepository.findByKeyword(keyword);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm blog posts với từ khóa: {}", keyword, e);
            throw new RuntimeException("Không thể tìm kiếm blog posts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostDto> searchBlogPostsWithPagination(String keyword, Pageable pageable) {
        log.info("Tìm kiếm blog posts với từ khóa và phân trang: keyword={}, page={}, size={}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BlogPost> blogPostPage = blogRepository.findByKeyword(keyword, pageable);
            return blogPostPage.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm blog posts với từ khóa và phân trang: {}", keyword, e);
            throw new RuntimeException("Không thể tìm kiếm blog posts với phân trang", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalBlogPostsCount() {
        log.info("Đếm tổng số blog posts");
        try {
            return blogRepository.countAllBlogPosts();
        } catch (Exception e) {
            log.error("Lỗi khi đếm tổng số blog posts", e);
            throw new RuntimeException("Không thể đếm số lượng blog posts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> getRelatedBlogPosts(Long currentBlogId, int authorId, int limit) {
        log.info("Lấy {} blog posts liên quan cho blog ID: {} của tác giả: {}", limit, currentBlogId, authorId);
        try {
            List<BlogPost> allAuthorPosts = blogRepository.findByAuthorId(authorId);
            return allAuthorPosts.stream()
                    .filter(post -> !post.getId().equals(currentBlogId))
                    .limit(limit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy blog posts liên quan", e);
            throw new RuntimeException("Không thể lấy blog posts liên quan", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.info("Kiểm tra blog post tồn tại với ID: {}", id);
        try {
            return blogRepository.existsById(id);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra blog post tồn tại với ID: {}", id, e);
            return false;
        }
    }

    /**
     * Chuyển đổi BlogPost entity thành BlogPostDto.
     *
     * @param blogPost BlogPost entity
     * @return BlogPostDto
     */
    private BlogPostDto convertToDto(BlogPost blogPost) {
        BlogPostDto dto = modelMapper.map(blogPost, BlogPostDto.class);

        // Thiết lập thông tin tác giả
        if (blogPost.getAuthor() != null) {
            dto.setAuthorId(blogPost.getAuthor().getId());
            dto.setAuthorName(blogPost.getAuthor().getUserName());
            dto.setAuthorUsername(blogPost.getAuthor().getUserName());
        }

        // Tính toán các thuộc tính phụ thuộc
        dto.generateSummary();
        dto.calculateEstimatedReadTime();

        return dto;
    }
}
