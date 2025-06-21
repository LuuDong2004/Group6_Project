package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleDto;
import group6.cinema_project.dto.SeatReservationDto;
import group6.cinema_project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

@Controller
@RequestMapping("seat")
public class SeatController {
    @Autowired
    private ISeatService seatService;
    
    @Autowired
    private ISeatReservationService seatReservationService;
    
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
            List<SeatReservationDto> seats = seatReservationService.getSeatsWithStatus(scheduleId);

            // Thêm dữ liệu vào model
            model.addAttribute("movies", movies);
            model.addAttribute("schedule", selectedSchedule);
            model.addAttribute("seats", seats);
            model.addAttribute("movieId", movieId);
            model.addAttribute("scheduleId", scheduleId);
            model.addAttribute("roomId", roomId);
            
            // Thêm danh sách phòng (chỉ có 1 phòng cho lịch chiếu này)
            if (selectedSchedule != null && selectedSchedule.getScreeningRoom() != null) {
                model.addAttribute("rooms", Collections.singletonList(selectedSchedule.getScreeningRoom()));
            }

            return "ticket-booking-seat";

        } catch (Exception e) {
            // Log error và redirect về trang lỗi
            model.addAttribute("error", "Không thể tải thông tin ghế: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(@RequestParam Integer seatId,
                                            @RequestParam Integer scheduleId,
                                            @RequestParam Integer userId) {
        try {
            boolean success = seatReservationService.reserveSeat(seatId, scheduleId, userId, null);
            if (success) {
                return ResponseEntity.ok("Ghế đã được giữ chỗ thành công");
            } else {
                return ResponseEntity.badRequest().body("Không thể giữ chỗ ghế này");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi giữ chỗ: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelReservation(@RequestParam Integer seatId,
                                                   @RequestParam Integer scheduleId,
                                                   @RequestParam Integer userId) {
        try {
            boolean success = seatReservationService.cancelPendingReservation(seatId, scheduleId, userId);
            if (success) {
                return ResponseEntity.ok("Đã hủy giữ chỗ thành công");
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy giao dịch giữ chỗ để hủy");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi hủy giữ chỗ: " + e.getMessage());
        }
    }

    @GetMapping("/get-by-room")
    @ResponseBody
    public ResponseEntity<List<SeatReservationDto>> getSeatsByRoomAndSchedule(
            @RequestParam Integer roomId,
            @RequestParam Integer scheduleId) {
        try {
            // Lấy danh sách ghế với trạng thái cho lịch chiếu
            List<SeatReservationDto> seats = seatReservationService.getSeatsWithStatus(scheduleId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<List<SeatReservationDto>> getSeatStatus(@RequestParam Integer scheduleId) {
        try {
            List<SeatReservationDto> seats = seatReservationService.getSeatsWithStatus(scheduleId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
}