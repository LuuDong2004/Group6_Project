package group6.cinema_project.controller.Admin;

import group6.cinema_project.dto.BlogPostDto;
import group6.cinema_project.entity.User;

import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.service.Admin.IAdminBlogService;
import group6.cinema_project.config.TinyMCEConfig;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Controller cho admin quản lý blog posts.
 * Xử lý các chức năng CRUD cho blog posts trong admin panel.
 */
@Controller
@RequestMapping("/admin/blogs")
@RequiredArgsConstructor
@Slf4j
public class AdminBlogController {

    private final IAdminBlogService adminBlogService;
    private final UserRepository userRepository;
    private final TinyMCEConfig tinyMCEConfig;

    /**
     * Hiển thị danh sách blog posts với tìm kiếm và phân trang.
     */
    @GetMapping("/list")
    public String listBlogPosts(Model model,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Admin: Hiển thị danh sách blog posts - page: {}, size: {}, searchTerm: {}", page, size, searchTerm);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BlogPostDto> blogPostPage;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                blogPostPage = adminBlogService.searchBlogPostsWithPaginationForAdmin(searchTerm, pageable);
                log.info("Tìm kiếm blog posts với từ khóa: {}", searchTerm);
            } else {
                blogPostPage = adminBlogService.getBlogPostsWithPaginationForAdmin(pageable);
            }

            model.addAttribute("blogPosts", blogPostPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", blogPostPage.getTotalPages());
            model.addAttribute("totalElements", blogPostPage.getTotalElements());
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");

            log.info("Đã tải {} blog posts cho trang {}", blogPostPage.getContent().size(), page);
            return "admin/admin_blog_list";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị danh sách blog posts", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách blog posts: " + e.getMessage());
            return "admin/admin_blog_list";
        }
    }

    /**
     * Hiển thị form thêm blog post mới.
     */
    @GetMapping("/add")
    public String showAddBlogForm(Model model) {
        log.info("Admin: Hiển thị form thêm blog post");

        try {
            model.addAttribute("blogPost", new BlogPostDto());

            // Thêm TinyMCE config
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());

            return "admin/admin_blog_add";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị form thêm blog post", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải form: " + e.getMessage());
            return "redirect:/admin/blogs/list";
        }
    }

    /**
     * Xử lý thêm blog post mới.
     */
    @PostMapping("/add")
    public String addBlogPost(@Valid @ModelAttribute("blogPost") BlogPostDto blogPostDto,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Admin: Xử lý thêm blog post với tiêu đề: {}", blogPostDto.getTitle());

        try {
            // Lấy user đang đăng nhập từ SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usernameOrEmail = authentication.getName();
            User currentUser = userRepository.findByUserName(usernameOrEmail)
                    .or(() -> userRepository.findByEmail(usernameOrEmail))
                    .orElse(null);
            if (currentUser == null) {
                log.error("Không tìm thấy user đang đăng nhập!");
                bindingResult.rejectValue("title", "error.blogPost",
                        "Không tìm thấy user đang đăng nhập. Vui lòng đăng nhập lại.");
                model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
                return "admin/admin_blog_add";
            }
            blogPostDto.setAuthorId(currentUser.getId());
            log.info("Đã gán tác giả: {} (ID: {}, Role: {}) cho blog post",
                    currentUser.getUserName(), currentUser.getId(), currentUser.getRole());
        } catch (Exception e) {
            log.error("Lỗi khi gán tác giả cho blog post", e);
            bindingResult.rejectValue("title", "error.blogPost",
                    "Có lỗi xảy ra khi xác định tác giả: " + e.getMessage());
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
            return "admin/admin_blog_add";
        }

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors trong form thêm blog post");
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
            return "admin/admin_blog_add";
        }

        try {
            // Kiểm tra trùng lặp tiêu đề
            if (adminBlogService.isTitleDuplicate(blogPostDto.getTitle())) {
                bindingResult.rejectValue("title", "error.blogPost", "Tiêu đề blog đã tồn tại");
                model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
                return "admin/admin_blog_add";
            }

            // Tạo blog post với ảnh
            BlogPostDto savedBlogPost = adminBlogService.createBlogPostWithImage(blogPostDto, coverImageFile);
            log.info("Đã tạo blog post thành công với ID: {}", savedBlogPost.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Blog post '" + savedBlogPost.getTitle() + "' đã được tạo thành công!");
            return "redirect:/admin/blogs/list";

        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh cho blog post", e);
            bindingResult.rejectValue("coverImageUrl", "error.blogPost", "Có lỗi xảy ra khi tải lên ảnh bìa");
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
            return "admin/admin_blog_add";
        } catch (Exception e) {
            log.error("Lỗi khi tạo blog post", e);
            bindingResult.rejectValue("title", "error.blogPost", "Có lỗi xảy ra khi tạo blog post: " + e.getMessage());
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());
            return "admin/admin_blog_add";
        }
    }

    /**
     * Hiển thị form chỉnh sửa blog post.
     */
    @GetMapping("/edit/{id}")
    public String showEditBlogForm(@PathVariable("id") Long id, Model model) {
        log.info("Admin: Hiển thị form chỉnh sửa blog post ID: {}", id);

        try {
            Optional<BlogPostDto> blogPost = adminBlogService.getBlogPostByIdForAdmin(id);
            if (blogPost.isEmpty()) {
                log.warn("Không tìm thấy blog post với ID: {}", id);
                model.addAttribute("error", "Không tìm thấy blog post");
                return "redirect:/admin/blogs/list";
            }

            model.addAttribute("blogPost", blogPost.get());

            // Thêm TinyMCE config
            model.addAttribute("tinyMCEUrl", tinyMCEConfig.getTinyMCEUrl());

            return "admin/admin_blog_edit";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị form chỉnh sửa blog post ID: {}", id, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải form chỉnh sửa: " + e.getMessage());
            return "redirect:/admin/blogs/list";
        }
    }

    /**
     * Xử lý cập nhật blog post.
     */
    @PostMapping("/edit/{id}")
    public String updateBlogPost(@PathVariable("id") Long id,
            @Valid @ModelAttribute("blogPost") BlogPostDto blogPostDto,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Admin: Xử lý cập nhật blog post ID: {} với tiêu đề: {}", id, blogPostDto.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors trong form cập nhật blog post");
            try {
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
            } catch (Exception e) {
                log.error("Lỗi khi reload users", e);
            }
            return "admin/admin_blog_edit";
        }

        try {
            // Kiểm tra trùng lặp tiêu đề (loại trừ blog post hiện tại)
            if (adminBlogService.isTitleDuplicateForUpdate(blogPostDto.getTitle(), id)) {
                bindingResult.rejectValue("title", "error.blogPost", "Tiêu đề blog đã tồn tại");
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
                return "admin/admin_blog_edit";
            }

            // Cập nhật blog post với ảnh
            BlogPostDto updatedBlogPost = adminBlogService.updateBlogPostWithImage(id, blogPostDto, coverImageFile);
            log.info("Đã cập nhật blog post thành công với ID: {}", updatedBlogPost.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Blog post '" + updatedBlogPost.getTitle() + "' đã được cập nhật thành công!");
            return "redirect:/admin/blogs/list";

        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh cho blog post", e);
            bindingResult.rejectValue("coverImageUrl", "error.blogPost", "Có lỗi xảy ra khi tải lên ảnh bìa");
            try {
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
            } catch (Exception ex) {
                log.error("Lỗi khi reload users", ex);
            }
            return "admin/admin_blog_edit";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật blog post ID: {}", id, e);
            bindingResult.rejectValue("title", "error.blogPost",
                    "Có lỗi xảy ra khi cập nhật blog post: " + e.getMessage());
            try {
                List<User> users = userRepository.findAll();
                model.addAttribute("users", users);
            } catch (Exception ex) {
                log.error("Lỗi khi reload users", ex);
            }
            return "admin/admin_blog_edit";
        }
    }

    /**
     * Hiển thị trang xác nhận xóa blog post.
     */
    @GetMapping("/delete/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Long id, Model model) {
        log.info("Admin: Hiển thị trang xác nhận xóa blog post ID: {}", id);

        try {
            Optional<BlogPostDto> blogPost = adminBlogService.getBlogPostByIdForAdmin(id);
            if (blogPost.isEmpty()) {
                log.warn("Không tìm thấy blog post với ID: {}", id);
                model.addAttribute("error", "Không tìm thấy blog post");
                return "redirect:/admin/blogs/list";
            }

            model.addAttribute("blogPost", blogPost.get());
            return "admin/admin_blog_delete_confirm";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị trang xác nhận xóa blog post ID: {}", id, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang xác nhận: " + e.getMessage());
            return "redirect:/admin/blogs/list";
        }
    }

    /**
     * Xóa blog post.
     */
    @PostMapping("/delete/{id}")
    public String deleteBlogPost(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("Admin: Xóa blog post ID: {}", id);

        try {
            boolean deleted = adminBlogService.deleteBlogPost(id);
            if (deleted) {
                log.info("Đã xóa blog post thành công với ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", "Blog post đã được xóa thành công!");
            } else {
                log.warn("Không tìm thấy blog post để xóa với ID: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy blog post để xóa");
            }

        } catch (Exception e) {
            log.error("Lỗi khi xóa blog post ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa blog post: " + e.getMessage());
        }

        return "redirect:/admin/blogs/list";
    }

    /**
     * Xem chi tiết blog post trong admin.
     */
    @GetMapping("/view/{id}")
    public String viewBlogPost(@PathVariable("id") Long id, Model model) {
        log.info("Admin: Xem chi tiết blog post ID: {}", id);

        try {
            Optional<BlogPostDto> blogPost = adminBlogService.getBlogPostByIdForAdmin(id);
            if (blogPost.isEmpty()) {
                log.warn("Không tìm thấy blog post với ID: {}", id);
                model.addAttribute("error", "Không tìm thấy blog post");
                return "redirect:/admin/blogs/list";
            }

            model.addAttribute("blogPost", blogPost.get());
            return "admin/admin_blog_view";

        } catch (Exception e) {
            log.error("Lỗi khi xem chi tiết blog post ID: {}", id, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải blog post: " + e.getMessage());
            return "redirect:/admin/blogs/list";
        }
    }
}