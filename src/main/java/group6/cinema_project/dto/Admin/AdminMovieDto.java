package group6.cinema_project.dto.Admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * DTO cho Admin quản lý phim
 * Chứa đầy đủ thông tin cần thiết cho việc quản lý phim
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminMovieDto {
    private Integer id;
    
    @NotBlank(message = "Tên phim không được để trống.")
    @Size(max = 255, message = "Tên phim không được vượt quá 255 ký tự.")
    private String name;

    @NotBlank(message = "Mô tả không được để trống.")
    private String description;
    
    private String image;

    @NotNull(message = "Thời lượng không được để trống.")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0.")
    private Integer duration;

    @NotNull(message = "Ngày phát hành không được để trống.")
    @PastOrPresent(message = "Ngày phát hành không được là một ngày trong tương lai.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    // ID của Rating entity
    private Integer ratingId;

    // Danh sách ID của Genre entities
    @NotNull(message = "Phim phải có ít nhất một thể loại.")
    private Set<Integer> genreIds;

    private String language;
    private String trailer;
    private String status;

    // Danh sách tên các đạo diễn, không phải đối tượng Director
    private Set<String> directors;

    // Danh sách tên các diễn viên, không phải đối tượng Actor
    private Set<String> actors;

    // Các field để hiển thị trong admin template
    private String ratingDisplay; // Hiển thị rating dạng "G - Mọi lứa tuổi"
    private String genreDisplay; // Hiển thị genre dạng "Hành động, Hài kịch"
    
    // Thông tin thống kê cho admin
    private Integer totalSchedules; // Tổng số suất chiếu
    private Integer totalBookings; // Tổng số booking
    private Double totalRevenue; // Tổng doanh thu
    
    // Thông tin quản lý
    private Date createdDate; // Ngày tạo
    private Date updatedDate; // Ngày cập nhật
    private String createdBy; // Người tạo
    private String updatedBy; // Người cập nhật
}
