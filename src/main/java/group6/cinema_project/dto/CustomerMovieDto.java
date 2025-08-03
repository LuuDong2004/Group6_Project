package group6.cinema_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * DTO cho customer xem thông tin phim
 * Chứa các field cần thiết cho hiển thị và chi tiết phim
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomerMovieDto {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private Integer duration;
    private Date releaseDate;
    private String language;
    private String trailer;
    private String status;

    // Các field để hiển thị thông tin đã được format
    private String ratingDisplay; // Hiển thị rating dạng "G - Mọi lứa tuổi"
    private String genreDisplay; // Hiển thị genre dạng "Hành động, Hài kịch"

    // Danh sách đạo diễn với thông tin chi tiết (sử dụng PersonSimpleDto)
    private List<PersonSimpleDto> directorsDetail;

    // Danh sách diễn viên với thông tin chi tiết (sử dụng PersonSimpleDto)
    private List<PersonSimpleDto> actorsDetail;

    // Danh sách review cho phim
    private List<ReviewDto> reviews;
}
