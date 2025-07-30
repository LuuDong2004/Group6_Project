
package group6.cinema_project.controller.User;

import group6.cinema_project.dto.BlogPostDto;
import group6.cinema_project.service.User.IBlogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller cho user xem blog posts.
 * Xử lý các chức năng hiển thị blog cho người dùng cuối.
 */
@Controller
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

    private final IBlogService blogService;

    /**
     * Hiển thị danh sách blog posts với tìm kiếm và phân trang.
     */
    @GetMapping
    public String listBlogPosts(Model model,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "6") int size) {
        log.info("Hiển thị danh sách blog posts - page: {}, size: {}, search: {}", page, size, search);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BlogPostDto> blogPostPage;

            if (search != null && !search.trim().isEmpty()) {
                blogPostPage = blogService.searchBlogPostsWithPagination(search, pageable);
                log.info("Tìm kiếm blog posts với từ khóa: {}", search);
            } else {
                blogPostPage = blogService.getBlogPostsWithPagination(pageable);
            }

            // Lấy blog posts nổi bật (3 bài mới nhất)
            List<BlogPostDto> featuredPosts = blogService.getLatestBlogPosts(3);

            model.addAttribute("blogPosts", blogPostPage.getContent());
            model.addAttribute("featuredPosts", featuredPosts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", blogPostPage.getTotalPages());
            model.addAttribute("totalElements", blogPostPage.getTotalElements());
            model.addAttribute("search", search != null ? search : "");
            model.addAttribute("hasNext", blogPostPage.hasNext());
            model.addAttribute("hasPrevious", blogPostPage.hasPrevious());

            log.info("Đã tải {} blog posts cho trang {}", blogPostPage.getContent().size(), page);
            return "blog_list";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị danh sách blog posts", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách blog posts");
            model.addAttribute("blogPosts", List.of());
            model.addAttribute("featuredPosts", List.of());
            return "blog_list";
        }
    }

    /**
     * Hiển thị chi tiết blog post.
     */
    @GetMapping("/{id}")
    public String viewBlogPost(@PathVariable("id") Long id, Model model) {
        log.info("Xem chi tiết blog post ID: {}", id);

        try {
            Optional<BlogPostDto> blogPost = blogService.getBlogPostById(id);
            if (blogPost.isEmpty()) {
                log.warn("Không tìm thấy blog post với ID: {}", id);
                model.addAttribute("error", "Không tìm thấy bài viết");
                return "redirect:/blogs";
            }

            BlogPostDto post = blogPost.get();

            // Lấy bài viết liên quan (cùng tác giả, loại trừ bài hiện tại)
            List<BlogPostDto> relatedPosts = blogService.getRelatedBlogPosts(id, post.getAuthorId(), 4);

            // Lấy bài viết mới nhất (để hiển thị trong sidebar)
            List<BlogPostDto> latestPosts = blogService.getLatestBlogPosts(5);

            model.addAttribute("blogPost", post);
            model.addAttribute("relatedPosts", relatedPosts);
            model.addAttribute("latestPosts", latestPosts);

            log.info("Đã tải blog post: {}", post.getTitle());
            return "blog_detail";

        } catch (Exception e) {
            log.error("Lỗi khi xem chi tiết blog post ID: {}", id, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải bài viết");
            return "redirect:/blogs";
        }
    }

    /**
     * Tìm kiếm blog posts (API endpoint cho AJAX).
     */
    @GetMapping("/search")
    @ResponseBody
    public Page<BlogPostDto> searchBlogPosts(@RequestParam("q") String query,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "6") int size) {
        log.info("API tìm kiếm blog posts với từ khóa: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            return blogService.searchBlogPostsWithPagination(query, pageable);
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm blog posts", e);
            return Page.empty();
        }
    }

    /**
     * Lấy blog posts theo tác giả.
     */
    @GetMapping("/author/{authorId}")
    public String getBlogPostsByAuthor(@PathVariable("authorId") int authorId,
                                       Model model,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "6") int size) {
        log.info("Lấy blog posts theo tác giả ID: {}", authorId);

        try {
            // Lấy tất cả bài viết của tác giả
            List<BlogPostDto> authorPosts = blogService.findBlogPostsByAuthor(authorId);

            // Thực hiện phân trang thủ công
            int start = page * size;
            int end = Math.min(start + size, authorPosts.size());
            List<BlogPostDto> pagedPosts = authorPosts.subList(start, end);

            int totalPages = (int) Math.ceil((double) authorPosts.size() / size);

            // Lấy thông tin tác giả từ bài viết đầu tiên
            String authorName = authorPosts.isEmpty() ? "Không xác định" : authorPosts.get(0).getAuthorName();

            // Lấy blog posts nổi bật
            List<BlogPostDto> featuredPosts = blogService.getLatestBlogPosts(3);

            model.addAttribute("blogPosts", pagedPosts);
            model.addAttribute("featuredPosts", featuredPosts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", (long) authorPosts.size());
            model.addAttribute("authorName", authorName);
            model.addAttribute("authorId", authorId);
            model.addAttribute("hasNext", page < totalPages - 1);
            model.addAttribute("hasPrevious", page > 0);

            log.info("Đã tải {} blog posts của tác giả {} cho trang {}", pagedPosts.size(), authorName, page);
            return "blog_author";

        } catch (Exception e) {
            log.error("Lỗi khi lấy blog posts theo tác giả ID: {}", authorId, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải bài viết của tác giả");
            return "redirect:/blogs";
        }
    }

    /**
     * Lấy blog posts mới nhất (API endpoint).
     */
    @GetMapping("/latest")
    @ResponseBody
    public List<BlogPostDto> getLatestBlogPosts(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        log.info("API lấy {} blog posts mới nhất", limit);

        try {
            return blogService.getLatestBlogPosts(limit);
        } catch (Exception e) {
            log.error("Lỗi khi lấy blog posts mới nhất", e);
            return List.of();
        }
    }

    /**
     * Load more blog posts (API endpoint cho infinite scroll).
     */
    @GetMapping("/load-more")
    @ResponseBody
    public Page<BlogPostDto> loadMoreBlogPosts(@RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(value = "size", defaultValue = "6") int size,
                                               @RequestParam(value = "search", required = false) String search) {
        log.info("API load more blog posts - page: {}, size: {}, search: {}", page, size, search);

        try {
            Pageable pageable = PageRequest.of(page, size);

            if (search != null && !search.trim().isEmpty()) {
                return blogService.searchBlogPostsWithPagination(search, pageable);
            } else {
                return blogService.getBlogPostsWithPagination(pageable);
            }
        } catch (Exception e) {
            log.error("Lỗi khi load more blog posts", e);
            return Page.empty();
        }
    }

    /**
     * Trang chủ blog (redirect đến danh sách).
     */
    @GetMapping("/home")
    public String blogHome() {
        return "redirect:/blogs";
    }

    /**
     * Xử lý lỗi 404 cho blog.
     */
    @GetMapping("/not-found")
    public String blogNotFound(Model model) {
        model.addAttribute("error", "Không tìm thấy bài viết");
        return "blog_error";
    }
}
