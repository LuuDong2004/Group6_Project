package group6.cinema_project.service.Admin;

import group6.cinema_project.entity.Booking;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Interface cho Admin Statistic Service.
 * Định nghĩa các phương thức để admin xem thống kê hệ thống.
 */
public interface IAdminStatisticService {

    /**
     * Lấy tất cả booking
     * @return Danh sách tất cả booking
     */
    List<Booking> getAllBookings();

    /**
     * Lấy tất cả phim
     * @return Danh sách tất cả phim
     */
    List<Movie> getAllMovies();

    /**
     * Lấy tất cả lịch chiếu
     * @return Danh sách tất cả lịch chiếu
     */
    List<ScreeningSchedule> getAllScreeningSchedules();

    /**
     * Lấy tất cả người dùng
     * @return Danh sách tất cả người dùng
     */
    List<User> getAllUsers();

    /**
     * Lấy thống kê doanh thu theo phim
     * @return Danh sách Object[] chứa [tên phim, doanh thu]
     */
    List<Object[]> getMovieRevenueStats();

    /**
     * Lấy số lượng vé theo lịch chiếu
     * @return Map với key là ID lịch chiếu, value là số lượng vé
     */
    Map<Integer, Long> getTicketCountByScreeningSchedule();

    /**
     * Lấy thống kê tổng quan của hệ thống
     * @return Map chứa các thống kê tổng quan
     */
    Map<String, Object> getUserMovieStatistics();

    /**
     * Lấy danh sách thống kê user-movie để hiển thị trong bảng
     * @return Danh sách thống kê user-movie
     */
    List<Map<String, Object>> getUserMovieStatisticsList();

    /**
     * Lấy labels cho biểu đồ doanh thu phim
     * @return Danh sách tên phim
     */
    List<String> getMovieRevenueLabels();

    /**
     * Lấy data cho biểu đồ doanh thu phim
     * @return Danh sách doanh thu tương ứng với labels
     */
    List<Double> getMovieRevenueData();
}
