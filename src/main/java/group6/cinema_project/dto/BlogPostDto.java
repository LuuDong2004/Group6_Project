package group6.cinema_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO cho BlogPost entity.
 * Sử dụng để truyền dữ liệu giữa các layer và validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDto {

    /**
     * ID của blog post.
     */
    private Long id;

    /**
     * Tiêu đề của bài viết.
     * Bắt buộc và có độ dài từ 5 đến 255 ký tự.
     */
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề phải có độ dài từ 5 đến 255 ký tự")
    private String title;

    /**
     * Nội dung chi tiết của bài viết.
     * Bắt buộc và có độ dài tối thiểu 10 ký tự.
     */
    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
    private String content;

    /**
     * URL ảnh bìa của bài viết.
     */
    private String coverImageUrl;

    /**
     * ID của tác giả.
     * Tự động gán bởi hệ thống.
     */
    private int authorId;

    /**
     * Tên tác giả (chỉ đọc).
     */
    private String authorName;

    /**
     * Username của tác giả (chỉ đọc).
     */
    private String authorUsername;

    /**
     * Thời gian tạo bài viết.
     */
    private LocalDateTime createdAt;

    /**
     * Tóm tắt nội dung (200 ký tự đầu).
     */
    private String summary;

    /**
     * Số từ trong nội dung.
     */
    private Integer wordCount;

    /**
     * Thời gian đọc ước tính (phút).
     */
    private Integer estimatedReadTime;

    /**
     * Constructor để tạo DTO từ các thông tin cơ bản.
     * 
     * @param id            ID của blog post
     * @param title         Tiêu đề
     * @param content       Nội dung
     * @param coverImageUrl URL ảnh bìa
     * @param authorId      ID tác giả
     * @param authorName    Tên tác giả
     * @param createdAt     Thời gian tạo
     */
    public BlogPostDto(Long id, String title, String content, String coverImageUrl,
            int authorId, String authorName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.generateSummary();
        this.calculateEstimatedReadTime();
    }

    /**
     * Tạo tóm tắt từ nội dung (200 ký tự đầu, loại bỏ HTML tags).
     */
    public void generateSummary() {
        if (this.content != null && !this.content.trim().isEmpty()) {
            // Loại bỏ HTML tags trước khi tạo tóm tắt
            String plainText = this.content.replaceAll("<[^>]*>", "").trim();

            if (plainText.length() > 200) {
                this.summary = plainText.substring(0, 200) + "...";
            } else {
                this.summary = plainText;
            }
        } else {
            this.summary = "";
        }
    }

    /**
     * Tính thời gian đọc ước tính (giả sử 200 từ/phút).
     */
    public void calculateEstimatedReadTime() {
        if (this.content != null && !this.content.trim().isEmpty()) {
            // Loại bỏ HTML tags và đếm từ để tính thời gian đọc
            String plainText = this.content.replaceAll("<[^>]*>", "");
            String[] words = plainText.trim().split("\\s+");
            int wordCount = words.length;
            this.estimatedReadTime = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        } else {
            this.estimatedReadTime = 1;
        }
    }

    /**
     * Setter cho content với tự động tính toán các thuộc tính phụ thuộc.
     * 
     * @param content Nội dung mới
     */
    public void setContent(String content) {
        this.content = content;
        this.generateSummary();
        this.calculateEstimatedReadTime();
    }
}
