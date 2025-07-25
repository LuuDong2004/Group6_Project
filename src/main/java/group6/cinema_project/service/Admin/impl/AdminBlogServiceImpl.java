package group6.cinema_project.service.Admin.impl;



import group6.cinema_project.dto.BlogPostDto;
import group6.cinema_project.entity.BlogPost;
import group6.cinema_project.entity.User;

import group6.cinema_project.repository.Admin.AdminBlogRepository;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.service.Admin.IAdminBlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation của IAdminBlogService.
 * Xử lý logic nghiệp vụ cho admin quản lý blog posts.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminBlogServiceImpl implements IAdminBlogService {

    private final AdminBlogRepository adminBlogRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/blogs/";

    /**
     * Lấy tất cả blog posts cho admin
     * Sử dụng: adminBlogRepository.findAllWithAuthorOrderByIdAsc() từ
     * AdminBlogRepository
     *
     * @return Danh sách blog posts sắp xếp theo ID tăng dần
     */
    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> getAllBlogPostsForAdmin() {
        log.info("Admin: Lấy tất cả blog posts");
        try {
            List<BlogPost> blogPosts = adminBlogRepository.findAllWithAuthorOrderByIdAsc();
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi admin lấy tất cả blog posts", e);
            throw new RuntimeException("Không thể lấy danh sách blog posts", e);
        }
    }

    /**
     * Lấy blog posts với phân trang cho admin
     * Sử dụng: adminBlogRepository.findAllWithAuthorOrderByIdAsc() từ
     * AdminBlogRepository
     *
     * @param pageable Thông tin phân trang
     * @return Page chứa blog posts sắp xếp theo ID tăng dần
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostDto> getBlogPostsWithPaginationForAdmin(Pageable pageable) {
        log.info("Admin: Lấy blog posts với phân trang: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BlogPost> blogPostPage = adminBlogRepository.findAllWithAuthorOrderByIdAsc(pageable);
            return blogPostPage.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi admin lấy blog posts với phân trang", e);
            throw new RuntimeException("Không thể lấy danh sách blog posts với phân trang", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BlogPostDto> getBlogPostByIdForAdmin(Long id) {
        log.info("Admin: Lấy blog post theo ID: {}", id);
        try {
            Optional<BlogPost> blogPost = adminBlogRepository.findByIdWithAuthor(id);
            return blogPost.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi admin lấy blog post theo ID: {}", id, e);
            throw new RuntimeException("Không thể lấy blog post với ID: " + id, e);
        }
    }

    @Override
    public BlogPostDto createBlogPost(BlogPostDto blogPostDto) {
        log.info("Admin: Tạo blog post mới với tiêu đề: {}", blogPostDto.getTitle());
        try {
            // Validate dữ liệu
            List<String> errors = validateBlogPost(blogPostDto);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Dữ liệu không hợp lệ: " + String.join(", ", errors));
            }

            // Kiểm tra trùng lặp tiêu đề
            if (isTitleDuplicate(blogPostDto.getTitle())) {
                throw new IllegalArgumentException("Tiêu đề blog đã tồn tại");
            }

            // Tìm tác giả
            User author = userRepository.findById(blogPostDto.getAuthorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy tác giả với ID: " + blogPostDto.getAuthorId()));

            // Tạo entity
            BlogPost blogPost = new BlogPost();
            blogPost.setTitle(blogPostDto.getTitle());
            blogPost.setContent(blogPostDto.getContent());
            blogPost.setCoverImageUrl(blogPostDto.getCoverImageUrl());
            blogPost.setAuthor(author);

            // Lưu vào database
            BlogPost savedBlogPost = adminBlogRepository.save(blogPost);
            log.info("Đã tạo blog post thành công với ID: {}", savedBlogPost.getId());

            return convertToDto(savedBlogPost);
        } catch (Exception e) {
            log.error("Lỗi khi tạo blog post", e);
            throw new RuntimeException("Không thể tạo blog post", e);
        }
    }

    @Override
    public BlogPostDto updateBlogPost(Long id, BlogPostDto blogPostDto) {
        log.info("Admin: Cập nhật blog post ID: {} với tiêu đề: {}", id, blogPostDto.getTitle());
        try {
            // Tìm blog post hiện tại
            BlogPost existingBlogPost = adminBlogRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog post với ID: " + id));

            // Validate dữ liệu
            List<String> errors = validateBlogPost(blogPostDto);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Dữ liệu không hợp lệ: " + String.join(", ", errors));
            }

            // Kiểm tra trùng lặp tiêu đề (loại trừ blog post hiện tại)
            if (isTitleDuplicateForUpdate(blogPostDto.getTitle(), id)) {
                throw new IllegalArgumentException("Tiêu đề blog đã tồn tại");
            }

            // Tìm tác giả mới (nếu thay đổi)
            if (existingBlogPost.getAuthor().getId() != blogPostDto.getAuthorId()) {
                User newAuthor = userRepository.findById(blogPostDto.getAuthorId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Không tìm thấy tác giả với ID: " + blogPostDto.getAuthorId()));
                existingBlogPost.setAuthor(newAuthor);
            }

            // Cập nhật thông tin
            existingBlogPost.setTitle(blogPostDto.getTitle());
            existingBlogPost.setContent(blogPostDto.getContent());
            if (blogPostDto.getCoverImageUrl() != null) {
                existingBlogPost.setCoverImageUrl(blogPostDto.getCoverImageUrl());
            }

            // Lưu vào database
            BlogPost updatedBlogPost = adminBlogRepository.save(existingBlogPost);
            log.info("Đã cập nhật blog post thành công với ID: {}", updatedBlogPost.getId());

            return convertToDto(updatedBlogPost);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật blog post ID: {}", id, e);
            throw new RuntimeException("Không thể cập nhật blog post", e);
        }
    }

    @Override
    public boolean deleteBlogPost(Long id) {
        log.info("Admin: Xóa blog post ID: {}", id);
        try {
            Optional<BlogPost> blogPost = adminBlogRepository.findById(id);
            if (blogPost.isPresent()) {
                // Xóa ảnh bìa nếu có
                if (blogPost.get().getCoverImageUrl() != null && !blogPost.get().getCoverImageUrl().isEmpty()) {
                    deleteCoverImage(blogPost.get().getCoverImageUrl());
                }

                adminBlogRepository.deleteById(id);
                log.info("Đã xóa blog post thành công với ID: {}", id);
                return true;
            } else {
                log.warn("Không tìm thấy blog post với ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Lỗi khi xóa blog post ID: {}", id, e);
            throw new RuntimeException("Không thể xóa blog post", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> findBlogPostsByTitleForAdmin(String title) {
        log.info("Admin: Tìm blog posts theo tiêu đề: {}", title);
        try {
            List<BlogPost> blogPosts = adminBlogRepository.findByTitleContainingIgnoreCaseWithAuthor(title);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi admin tìm blog posts theo tiêu đề: {}", title, e);
            throw new RuntimeException("Không thể tìm blog posts theo tiêu đề", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> findBlogPostsByAuthorForAdmin(int authorId) {
        log.info("Admin: Tìm blog posts theo tác giả: {}", authorId);
        try {
            List<BlogPost> blogPosts = adminBlogRepository.findByAuthorIdWithAuthor(authorId);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi admin tìm blog posts theo tác giả: {}", authorId, e);
            throw new RuntimeException("Không thể tìm blog posts theo tác giả", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDto> searchBlogPostsForAdmin(String keyword) {
        log.info("Admin: Tìm kiếm blog posts với từ khóa: {}", keyword);
        try {
            List<BlogPost> blogPosts = adminBlogRepository.findByKeywordWithAuthor(keyword);
            return blogPosts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi admin tìm kiếm blog posts với từ khóa: {}", keyword, e);
            throw new RuntimeException("Không thể tìm kiếm blog posts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostDto> searchBlogPostsWithPaginationForAdmin(String keyword, Pageable pageable) {
        log.info("Admin: Tìm kiếm blog posts với từ khóa và phân trang: keyword={}, page={}, size={}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BlogPost> blogPostPage = adminBlogRepository.findByKeywordWithAuthor(keyword, pageable);
            return blogPostPage.map(this::convertToDto);
        } catch (Exception e) {
            log.error("Lỗi khi admin tìm kiếm blog posts với từ khóa và phân trang: {}", keyword, e);
            throw new RuntimeException("Không thể tìm kiếm blog posts với phân trang", e);
        }
    }

    @Override
    public String uploadCoverImage(MultipartFile imageFile) throws IOException {
        log.info("Upload ảnh bìa: {}", imageFile.getOriginalFilename());
        try {
            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Đã tạo thư mục upload: {}", uploadPath.toAbsolutePath());
            }

            // Tạo tên file unique
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/blogs/" + uniqueFilename;
            log.info("Đã upload ảnh thành công: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh: {}", imageFile.getOriginalFilename(), e);
            throw new IOException("Không thể upload ảnh", e);
        }
    }

    @Override
    public boolean deleteCoverImage(String imageUrl) {
        log.info("Xóa ảnh bìa: {}", imageUrl);
        try {
            if (imageUrl != null && imageUrl.startsWith("/uploads/blogs/")) {
                String filename = imageUrl.substring("/uploads/blogs/".length());
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Đã xóa ảnh thành công: {}", imageUrl);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.error("Lỗi khi xóa ảnh: {}", imageUrl, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTitleDuplicate(String title) {
        try {
            return adminBlogRepository.existsByTitleIgnoreCase(title);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra trùng lặp tiêu đề: {}", title, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTitleDuplicateForUpdate(String title, Long currentId) {
        try {
            return adminBlogRepository.existsByTitleIgnoreCaseAndIdNot(title, currentId);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra trùng lặp tiêu đề cho update: {}", title, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countBlogPostsByAuthor(int authorId) {
        try {
            return adminBlogRepository.countByAuthorId(authorId);
        } catch (Exception e) {
            log.error("Lỗi khi đếm blog posts theo tác giả: {}", authorId, e);
            return 0L;
        }
    }

    @Override
    public List<String> validateBlogPost(BlogPostDto blogPostDto) {
        List<String> errors = new ArrayList<>();

        if (blogPostDto.getTitle() == null || blogPostDto.getTitle().trim().isEmpty()) {
            errors.add("Tiêu đề không được để trống");
        } else if (blogPostDto.getTitle().length() < 5 || blogPostDto.getTitle().length() > 255) {
            errors.add("Tiêu đề phải có độ dài từ 5 đến 255 ký tự");
        }

        if (blogPostDto.getContent() == null || blogPostDto.getContent().trim().isEmpty()) {
            errors.add("Nội dung không được để trống");
        } else if (blogPostDto.getContent().length() < 10) {
            errors.add("Nội dung phải có ít nhất 10 ký tự");
        }

        if (blogPostDto.getAuthorId() <= 0) {
            errors.add("Tác giả không được để trống");
        }

        return errors;
    }

    @Override
    public BlogPostDto createBlogPostWithImage(BlogPostDto blogPostDto, MultipartFile coverImageFile)
            throws IOException {
        log.info("Tạo blog post với ảnh bìa");
        try {
            // Upload ảnh nếu có
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String imageUrl = uploadCoverImage(coverImageFile);
                blogPostDto.setCoverImageUrl(imageUrl);
            }

            return createBlogPost(blogPostDto);
        } catch (IOException e) {
            log.error("Lỗi khi tạo blog post với ảnh", e);
            throw e;
        }
    }

    @Override
    public BlogPostDto updateBlogPostWithImage(Long id, BlogPostDto blogPostDto, MultipartFile coverImageFile)
            throws IOException {
        log.info("Cập nhật blog post với ảnh bìa mới");
        try {
            // Lấy blog post hiện tại để xóa ảnh cũ nếu cần
            Optional<BlogPost> existingBlogPost = adminBlogRepository.findById(id);
            String oldImageUrl = null;
            if (existingBlogPost.isPresent()) {
                oldImageUrl = existingBlogPost.get().getCoverImageUrl();
            }

            // Upload ảnh mới nếu có
            if (coverImageFile != null && !coverImageFile.isEmpty()) {
                String newImageUrl = uploadCoverImage(coverImageFile);
                blogPostDto.setCoverImageUrl(newImageUrl);

                // Xóa ảnh cũ
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    deleteCoverImage(oldImageUrl);
                }
            }

            return updateBlogPost(id, blogPostDto);
        } catch (IOException e) {
            log.error("Lỗi khi cập nhật blog post với ảnh", e);
            throw e;
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