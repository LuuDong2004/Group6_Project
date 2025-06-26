package group6.cinema_project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * Data Transfer Object cho Movie.
 * Được tối ưu cho việc hiển thị và nhận dữ liệu từ form.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Integer id;

    @NotBlank(message = "Tên phim không được để trống.")
    @Size(max = 255, message = "Tên phim không được vượt quá 255 ký tự.")
    private String name;

    @NotBlank(message = "Mô tả không được để trống.")
    private String description;

    @NotNull(message = "Thời lượng không được để trống.")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0.")
    private Integer duration;

    @NotNull(message = "Ngày phát hành không được để trống.")
    @PastOrPresent(message = "Ngày phát hành không được là một ngày trong tương lai.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @NotBlank(message = "Xếp hạng không được để trống.")
    private String rating;

    // Giữ genre là String để khớp với Entity, việc xử lý nhiều thể loại sẽ thực
    // hiện ở front-end hoặc service
    @NotBlank(message = "Phim phải có ít nhất một thể loại.")
    private String genre;

    private String language;

    private String image;
    private String trailer;

    // Danh sách tên các đạo diễn, không phải đối tượng Director
    private Set<String> directors;

    // Danh sách tên các diễn viên, không phải đối tượng Actor
    private Set<String> actors;
}
