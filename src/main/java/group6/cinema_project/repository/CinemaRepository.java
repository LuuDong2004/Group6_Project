package group6.cinema_project.repository;

import group6.cinema_project.entity.Cinema;
import java.util.Arrays;
import java.util.List;

public class CinemaRepository {
    public List<Cinema> findAll() {
        // Danh sách mẫu, có thể thay bằng truy vấn DB nếu dùng JPA
        return Arrays.asList(
            new Cinema("CGV Nguyễn Chí Thanh", "54A Nguyễn Chí Thanh, Đống Đa, Hà Nội", 21.028511, 105.804817),
            new Cinema("Lotte Gò Vấp", "242 Nguyễn Văn Lượng, Gò Vấp, TP.HCM", 10.835651, 106.668877),
            new Cinema("Galaxy Tân Bình", "246 Nguyễn Hồng Đào, Tân Bình, TP.HCM", 10.793902, 106.652219)
        );
    }
} 