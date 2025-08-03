package group6.cinema_project.dto;

import group6.cinema_project.entity.ScreeningSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Date;

/**
 * DTO để hiển thị thông tin phim kèm theo danh sách lịch chiếu
 * Sử dụng cho trang admin_schedules_list.html
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithSchedulesDto {
    
    // Thông tin cơ bản của phim
    private Integer id;
    private String name;
    private String image;
    private Integer duration; // thời lượng tính bằng phút
    private Date releaseDate;
    private String rating;
    private String genre; // thể loại phim
    private String language;
    private String trailer;
    private String description;
    private String status;
    
    // Danh sách lịch chiếu của phim trong ngày được chọn
    private List<ScreeningSchedule> schedules;
    
    /**
     * Constructor để tạo từ Movie entity
     */
    public MovieWithSchedulesDto(Integer id, String name, String image, Integer duration, 
                                Date releaseDate, String rating, String genre, String language, 
                                String trailer, String description, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.genre = genre;
        this.language = language;
        this.trailer = trailer;
        this.description = description;
        this.status = status;
    }
    
    /**
     * Kiểm tra xem phim có lịch chiếu không
     */
    public boolean hasSchedules() {
        return schedules != null && !schedules.isEmpty();
    }
    
    /**
     * Lấy số lượng lịch chiếu
     */
    public int getScheduleCount() {
        return schedules != null ? schedules.size() : 0;
    }
    
    /**
     * Lấy danh sách lịch chiếu theo trạng thái
     */
    public List<ScreeningSchedule> getSchedulesByStatus(String status) {
        if (schedules == null || status == null) {
            return List.of();
        }
        
        return schedules.stream()
                .filter(schedule -> status.equalsIgnoreCase(schedule.getStatus()))
                .toList();
    }
}
