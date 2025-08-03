package group6.cinema_project.dto.Customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

/**
 * DTO cho Customer xem thông tin phim
 * Chỉ chứa thông tin cần thiết cho việc hiển thị và đặt vé
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomerMovieDto {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private Integer duration; // Thời lượng tính bằng phút
    private Date releaseDate;
    private String language;
    private String trailer;
    private String status;

    // Thông tin hiển thị đã được format
    private String ratingDisplay; // Hiển thị rating dạng "G - Mọi lứa tuổi"
    private String genreDisplay; // Hiển thị genre dạng "Hành động, Hài kịch"
    
    // Danh sách tên các đạo diễn
    private Set<String> directors;
    
    // Danh sách tên các diễn viên
    private Set<String> actors;
    
    // Thông tin bổ sung cho customer
    private String durationDisplay; // Hiển thị thời lượng dạng "2h 30m"
    private String releaseDateDisplay; // Hiển thị ngày phát hành đã format
    private boolean isAvailableForBooking; // Có thể đặt vé hay không
    private Double averageRating; // Điểm đánh giá trung bình
    private Integer totalReviews; // Tổng số đánh giá
}
