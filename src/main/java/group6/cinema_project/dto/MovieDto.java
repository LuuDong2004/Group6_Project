package group6.cinema_project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Set;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieDto {
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
    
    // Danh sách đạo diễn với thông tin chi tiết
    private List<PersonSimpleDto> directors;

    // Danh sách diễn viên với thông tin chi tiết
    private List<PersonSimpleDto> actors;
    
    // Danh sách review
    private List<ReviewDto> reviews;
}