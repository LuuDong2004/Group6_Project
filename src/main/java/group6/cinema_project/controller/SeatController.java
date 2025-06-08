package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleDto;
import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.service.IMovieService;
import group6.cinema_project.service.IScheduleService;
import group6.cinema_project.service.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("seat")
public class SeatController {
    @Autowired
    private ISeatService seatService;
    @Autowired
    private IMovieService movieService;
    @Autowired
    private IScheduleService scheduleService;

    @GetMapping("/{movieId}/{scheduleId}/{roomId}")
    public String getSeatsByMovieId(@PathVariable(name = "movieId") Integer movieId,
                                    @PathVariable(name = "scheduleId") Integer scheduleId,
                                    @PathVariable(name = "roomId") Integer roomId,
                                    Model model) {
        try {
            // Lấy thông tin phim
            List<MovieDto> movies = movieService.findMovieById(movieId);

            // Lấy thông tin lịch chiếu
            ScheduleDto selectedSchedule = scheduleService.getScheduleById(scheduleId);

            // Lấy danh sách ghế với trạng thái
            List<SeatDto> seats = seatService.getSeatsWithStatusByRoomAndSchedule(roomId, scheduleId);

            // Thêm dữ liệu vào model
            model.addAttribute("movies", movies);
            model.addAttribute("schedule", selectedSchedule);
            model.addAttribute("seats", seats);
            model.addAttribute("movieId", movieId);
            model.addAttribute("scheduleId", scheduleId);
            model.addAttribute("roomId", roomId);

            return "ticket-booking-seat";

        } catch (Exception e) {
            // Log error và redirect về trang lỗi
            model.addAttribute("error", "Không thể tải thông tin ghế: " + e.getMessage());
            return "error";
        }
    }
}