package group6.cinema_project.controller.Admin;

import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.exception.ScheduleConflictException;
import group6.cinema_project.service.Admin.IAdminScheduleService;
import group6.cinema_project.repository.Admin.AdminRoomRepository;
import group6.cinema_project.repository.Admin.AdminMovieRepository;
import group6.cinema_project.repository.Admin.AdminBranchRepository;
import group6.cinema_project.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller quản lý lịch chiếu phim cho admin
 */
@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleController {

    private final IAdminScheduleService adminScheduleService;
    private final AdminRoomRepository roomRepository;
    private final AdminMovieRepository movieRepository;
    private final AdminBranchRepository branchRepository;
    private final BookingRepository bookingRepository;

    /**
     * Hiển thị trang lịch chiếu tổng quan (giao diện lịch)
     */
    @GetMapping("/list")
    public String showScheduleCalendar(Model model) {
        log.info("Hiển thị trang lịch chiếu tổng quan");

        // Lấy tháng và năm hiện tại
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("currentYear", currentDate.getYear());
        model.addAttribute("currentMonth", currentDate.getMonthValue());

        return "admin/admin_schedules_list";
    }

    /**
     * Hiển thị trang timeline chi tiết (giao diện timeline)
     */
    @GetMapping("/detail")
    public String showScheduleTimeline(@RequestParam(required = false) String date, Model model) {
        log.info("Hiển thị trang timeline chi tiết cho ngày: {}", date);

        // Nếu không có ngày được chọn, sử dụng ngày hiện tại
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("formattedDate", selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return "admin/admin_schedules_list_detail";
    }

    /**
     * API lấy dữ liệu lịch chiếu theo tháng cho calendar
     */
    @GetMapping("/api/calendar-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCalendarData(
            @RequestParam int year,
            @RequestParam int month) {

        try {
            log.info("Lấy dữ liệu lịch chiếu cho tháng {}/{}", month, year);

            // Tạo ngày đầu và cuối tháng
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // Lấy tất cả lịch chiếu trong tháng
            List<ScreeningScheduleDto> allSchedules = adminScheduleService.getAllScreeningSchedulesForDisplay();

            // Lọc lịch chiếu theo tháng
            List<ScreeningScheduleDto> monthSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        LocalDate scheduleDate = schedule.getScreeningDate();
                        return scheduleDate != null &&
                                !scheduleDate.isBefore(startDate) &&
                                !scheduleDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());

            // Nhóm lịch chiếu theo ngày
            Map<String, List<ScreeningScheduleDto>> schedulesByDate = monthSchedules.stream()
                    .collect(Collectors.groupingBy(
                            schedule -> schedule.getScreeningDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

            // Lấy danh sách phòng chiếu
            List<ScreeningRoom> rooms = roomRepository.findAll();

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("schedulesByDate", schedulesByDate);
            response.put("rooms", rooms);
            response.put("totalSchedules", monthSchedules.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu lịch chiếu: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi xảy ra khi tải dữ liệu lịch chiếu");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * API lấy dữ liệu lịch chiếu chi tiết cho một ngày cụ thể
     */
    @GetMapping("/api/timeline-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTimelineData(
            @RequestParam String date,
            @RequestParam(required = false) Integer branchId) {

        try {
            log.info("Lấy dữ liệu timeline cho ngày: {}, chi nhánh: {}", date, branchId);

            LocalDate selectedDate = LocalDate.parse(date);

            // Chuyển đổi LocalDate thành Date để tương thích với repository
            java.util.Date dateParam = java.sql.Date.valueOf(selectedDate);

            // Lấy lịch chiếu cho ngày được chọn, có thể filter theo chi nhánh
            List<ScreeningScheduleDto> daySchedules = adminScheduleService
                    .getFilteredScreeningSchedulesForDisplay(null, dateParam, null);

            // Filter theo chi nhánh nếu có
            if (branchId != null) {
                daySchedules = daySchedules.stream()
                        .filter(schedule -> schedule.getBranchId() != null && schedule.getBranchId().equals(branchId))
                        .collect(Collectors.toList());
            }

            // Lấy danh sách phòng chiếu theo chi nhánh
            List<ScreeningRoom> rooms;
            if (branchId != null) {
                rooms = roomRepository.findByBranchId(branchId);
            } else {
                rooms = roomRepository.findAll();
            }

            // Lấy danh sách tất cả chi nhánh
            List<group6.cinema_project.entity.Branch> branches = branchRepository.findAll();

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("schedules", daySchedules);
            response.put("rooms", rooms);
            response.put("branches", branches);
            response.put("selectedDate", date);
            response.put("selectedBranchId", branchId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu timeline: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi xảy ra khi tải dữ liệu timeline");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * API tìm kiếm lịch chiếu theo tên phim
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchSchedules(
            @RequestParam String date,
            @RequestParam(required = false) String movieName,
            @RequestParam(required = false) String status) {

        try {
            log.info("Tìm kiếm lịch chiếu cho ngày {} với phim '{}' và trạng thái '{}'",
                    date, movieName, status);

            LocalDate selectedDate = LocalDate.parse(date);

            // Chuyển đổi LocalDate thành Date để tương thích với repository
            java.util.Date dateParam = java.sql.Date.valueOf(selectedDate);

            // Lấy tất cả lịch chiếu cho ngày được chọn
            List<ScreeningScheduleDto> daySchedules = adminScheduleService
                    .getFilteredScreeningSchedulesForDisplay(null, dateParam, null);

            // Lọc theo tên phim nếu có
            if (movieName != null && !movieName.trim().isEmpty()) {
                String searchTerm = movieName.toLowerCase().trim();
                daySchedules = daySchedules.stream()
                        .filter(schedule -> {
                            String name = schedule.getMovieName();
                            return name != null && name.toLowerCase().contains(searchTerm);
                        })
                        .collect(Collectors.toList());
            }

            // Lọc theo trạng thái nếu có
            if (status != null && !status.trim().isEmpty() && !"ALL".equals(status)) {
                daySchedules = daySchedules.stream()
                        .filter(schedule -> status.equals(schedule.getStatus()))
                        .collect(Collectors.toList());
            }

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("schedules", daySchedules);
            response.put("totalFound", daySchedules.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm lịch chiếu: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi xảy ra khi tìm kiếm");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Hiển thị trang thêm lịch chiếu mới
     */
    @GetMapping("/add")
    public String showAddSchedulePage(
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) String branchName,
            Model model) {

        log.info(
                "Hiển thị trang thêm lịch chiếu - Room ID: {}, Room Name: {}, Date: {}, Branch ID: {}, Branch Name: {}",
                roomId, roomName, date, branchId, branchName);

        try {
            // Lấy danh sách tất cả phim để hiển thị trong autocomplete
            List<Movie> allMovies = movieRepository.findAllWithDirectorsAndActors();
            model.addAttribute("allMovies", allMovies);

            // Lấy danh sách tất cả phòng chiếu
            List<ScreeningRoom> allRooms = roomRepository.findAll();
            model.addAttribute("allRooms", allRooms);

            // Thông tin phòng và ngày được truyền từ timeline
            if (roomId != null) {
                model.addAttribute("selectedRoomId", roomId);
            }
            if (roomName != null) {
                model.addAttribute("selectedRoomName", roomName);
            }
            if (date != null) {
                model.addAttribute("selectedDate", date);
                // Format ngày để hiển thị
                LocalDate selectedDate = LocalDate.parse(date);
                model.addAttribute("formattedDate", selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            // Thông tin chi nhánh được truyền từ timeline
            if (branchId != null) {
                model.addAttribute("selectedBranchId", branchId);
            }
            if (branchName != null) {
                model.addAttribute("selectedBranchName", branchName);
            }

            return "admin/admin_schedule_add";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị trang thêm lịch chiếu: ", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang thêm lịch chiếu");
            return "redirect:/admin/schedules/detail" + (date != null ? "?date=" + date : "");
        }
    }

    /**
     * Xử lý thêm lịch chiếu mới (đơn lẻ hoặc nhiều cùng lúc)
     */
    @PostMapping("/add")
    public String addSchedule(
            @RequestParam Integer movieId,
            @RequestParam Integer screeningRoomId,
            @RequestParam String screeningDate,
            @RequestParam String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) List<String> additionalStartTimes,
            @RequestParam(required = false) List<String> additionalEndTimes,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(defaultValue = "UPCOMING") String status,
            RedirectAttributes redirectAttributes) {

        log.info(
                "Thêm lịch chiếu mới - Movie ID: {}, Room ID: {}, Date: {}, Start Time: {}, Branch ID: {}, Additional Times: {}",
                movieId, screeningRoomId, screeningDate, startTime, branchId, additionalStartTimes);

        try {
            // Kiểm tra xem có thêm nhiều lịch chiếu không
            boolean hasAdditionalSchedules = additionalStartTimes != null && !additionalStartTimes.isEmpty();

            if (hasAdditionalSchedules) {
                // Xử lý thêm nhiều lịch chiếu
                return addMultipleSchedules(movieId, screeningRoomId, screeningDate, startTime,
                        additionalStartTimes, additionalEndTimes, branchId, status, redirectAttributes);
            } else {
                // Xử lý thêm lịch chiếu đơn lẻ
                return addSingleSchedule(movieId, screeningRoomId, screeningDate, startTime,
                        branchId, status, redirectAttributes);
            }

        } catch (ScheduleConflictException e) {
            log.warn("Xung đột lịch chiếu: {}", e.getDetailedMessage());
            redirectAttributes.addFlashAttribute("error", e.getDetailedMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        } catch (IllegalArgumentException e) {
            log.warn("Dữ liệu không hợp lệ khi thêm lịch chiếu: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi thêm lịch chiếu: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm lịch chiếu. Vui lòng thử lại.");
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        }
    }

    /**
     * Xử lý thêm lịch chiếu đơn lẻ
     */
    private String addSingleSchedule(Integer movieId, Integer screeningRoomId, String screeningDate,
            String startTime, Integer branchId, String status, RedirectAttributes redirectAttributes) {

        try {
            // Tạo DTO cho lịch chiếu mới
            ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();
            scheduleDto.setMovieId(movieId);
            scheduleDto.setScreeningRoomId(screeningRoomId);
            scheduleDto.setScreeningDate(LocalDate.parse(screeningDate));
            scheduleDto.setStartTime(LocalTime.parse(startTime));
            scheduleDto.setBranchId(branchId);
            scheduleDto.setStatus(status);

            // Lưu lịch chiếu với validation
            ScreeningScheduleDto savedSchedule = adminScheduleService
                    .saveOrUpdateScreeningScheduleWithValidation(scheduleDto);

            log.info("Đã thêm lịch chiếu thành công với ID: {}", savedSchedule.getId());
            redirectAttributes.addFlashAttribute("success", "Thêm lịch chiếu thành công!");

            return "redirect:/admin/schedules/detail?date=" + screeningDate;

        } catch (ScheduleConflictException e) {
            log.warn("Xung đột lịch chiếu: {}", e.getDetailedMessage());
            redirectAttributes.addFlashAttribute("error", e.getDetailedMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        } catch (IllegalArgumentException e) {
            log.error("Dữ liệu không hợp lệ: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        }
    }

    /**
     * Xử lý thêm nhiều lịch chiếu cùng lúc
     */
    private String addMultipleSchedules(Integer movieId, Integer screeningRoomId, String screeningDate,
            String mainStartTime, List<String> additionalStartTimes, List<String> additionalEndTimes,
            Integer branchId, String status, RedirectAttributes redirectAttributes) {

        try {
            // Tạo base schedule
            ScreeningScheduleDto baseSchedule = new ScreeningScheduleDto();
            baseSchedule.setMovieId(movieId);
            baseSchedule.setScreeningRoomId(screeningRoomId);
            baseSchedule.setScreeningDate(LocalDate.parse(screeningDate));
            baseSchedule.setBranchId(branchId);
            baseSchedule.setStatus(status);

            // Tạo danh sách time slots (bao gồm cả main schedule)
            List<Map<String, Object>> timeSlots = new ArrayList<>();

            // Thêm main time slot
            Map<String, Object> mainSlot = new HashMap<>();
            mainSlot.put("startTime", mainStartTime);
            mainSlot.put("screeningRoomId", screeningRoomId.toString());
            timeSlots.add(mainSlot);

            // Thêm additional time slots
            for (int i = 0; i < additionalStartTimes.size(); i++) {
                if (additionalStartTimes.get(i) != null && !additionalStartTimes.get(i).trim().isEmpty()) {
                    Map<String, Object> slot = new HashMap<>();
                    slot.put("startTime", additionalStartTimes.get(i));
                    slot.put("screeningRoomId", screeningRoomId.toString());
                    timeSlots.add(slot);
                }
            }

            // Lưu batch schedules
            List<ScreeningScheduleDto> savedSchedules = adminScheduleService.saveBatchSchedules(baseSchedule,
                    timeSlots);

            log.info("Đã thêm {} lịch chiếu thành công", savedSchedules.size());
            redirectAttributes.addFlashAttribute("success",
                    "Thêm thành công " + savedSchedules.size() + " lịch chiếu!");

            return "redirect:/admin/schedules/detail?date=" + screeningDate;

        } catch (ScheduleConflictException e) {
            log.warn("Xung đột lịch chiếu khi thêm nhiều: {}", e.getDetailedMessage());
            redirectAttributes.addFlashAttribute("error", e.getDetailedMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        } catch (IllegalArgumentException e) {
            log.error("Dữ liệu không hợp lệ khi thêm nhiều: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedules/add?roomId=" + screeningRoomId + "&date=" + screeningDate;
        }
    }

    /**
     * API tìm kiếm phim cho autocomplete
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> searchMovies(@RequestParam String query) {
        try {
            List<Movie> movies = movieRepository.findByNameContainingIgnoreCaseWithDirectorsAndActors(query);

            List<Map<String, Object>> movieData = movies.stream()
                    .map(movie -> {
                        Map<String, Object> movieMap = new HashMap<>();
                        movieMap.put("id", movie.getId());
                        movieMap.put("name", movie.getName());
                        movieMap.put("duration", movie.getDuration());
                        movieMap.put("image", movie.getImage());
                        movieMap.put("genre", movie.getGenre());
                        movieMap.put("rating", movie.getRating());
                        return movieMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(movieData);

        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm phim: ", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * API kiểm tra trùng lặp lịch chiếu
     */
    @GetMapping("/api/check-conflict")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkScheduleConflict(
            @RequestParam Integer roomId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) Integer excludeScheduleId) {

        try {
            Map<String, Object> response = new HashMap<>();

            // Lấy lịch chiếu trong ngày cho phòng cụ thể
            LocalDate selectedDate = LocalDate.parse(date);
            java.util.Date dateParam = java.sql.Date.valueOf(selectedDate);

            List<ScreeningScheduleDto> existingSchedules = adminScheduleService
                    .getFilteredScreeningSchedulesForDisplay(null, dateParam, roomId);

            LocalTime newStartTime = LocalTime.parse(startTime);
            LocalTime newEndTime = LocalTime.parse(endTime);

            // Kiểm tra trùng lặp bao gồm cả trường hợp giờ kết thúc trùng với giờ bắt đầu
            // Loại trừ schedule hiện tại nếu đang edit
            boolean hasConflict = existingSchedules.stream()
                    .filter(schedule -> excludeScheduleId == null || !excludeScheduleId.equals(schedule.getId()))
                    .anyMatch(schedule -> {
                        LocalTime existingStart = schedule.getStartTime();
                        LocalTime existingEnd = schedule.getEndTime();

                        // Kiểm tra overlap bao gồm cả trường hợp trùng thời gian: (start1 <= end2) &&
                        // (start2 <= end1)
                        return (!newStartTime.isAfter(existingEnd) && !existingStart.isAfter(newEndTime));
                    });

            response.put("hasConflict", hasConflict);
            if (hasConflict) {
                response.put("message", "Phòng chiếu đã có lịch chiếu trong khung giờ này!");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra trùng lặp lịch chiếu: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("hasConflict", true);
            errorResponse.put("message", "Có lỗi xảy ra khi kiểm tra trùng lặp");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * API xóa lịch chiếu (chỉ cho phép xóa lịch chiếu có trạng thái UPCOMING)
     */
    @DeleteMapping("/api/delete/{scheduleId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSchedule(@PathVariable Integer scheduleId) {

        try {
            log.info("Yêu cầu xóa lịch chiếu với ID: {}", scheduleId);

            Map<String, Object> response = new HashMap<>();

            // Lấy thông tin lịch chiếu để kiểm tra trạng thái
            Optional<ScreeningScheduleDto> scheduleOptional = adminScheduleService.getScreeningScheduleById(scheduleId);

            if (scheduleOptional.isEmpty()) {
                log.warn("Không tìm thấy lịch chiếu với ID: {}", scheduleId);
                response.put("success", false);
                response.put("message", "Không tìm thấy lịch chiếu cần xóa");
                return ResponseEntity.ok(response);
            }

            ScreeningScheduleDto schedule = scheduleOptional.get();

            // Kiểm tra trạng thái - chỉ cho phép xóa lịch chiếu có trạng thái UPCOMING
            if (!"UPCOMING".equals(schedule.getStatus())) {
                log.warn("Không thể xóa lịch chiếu với trạng thái: {}", schedule.getStatus());
                response.put("success", false);
                response.put("message", "Chỉ có thể xóa lịch chiếu có trạng thái 'Sắp chiếu'");
                return ResponseEntity.ok(response);
            }

            // Thực hiện xóa lịch chiếu
            adminScheduleService.deleteScreeningSchedule(scheduleId);

            log.info("Đã xóa thành công lịch chiếu với ID: {}", scheduleId);
            response.put("success", true);
            response.put("message", "Xóa lịch chiếu thành công");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Lỗi khi xóa lịch chiếu: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        } catch (Exception e) {
            log.error("Lỗi không xác định khi xóa lịch chiếu: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi xảy ra khi xóa lịch chiếu");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Cập nhật thủ công trạng thái lịch chiếu
     * - UPCOMING -> ACTIVE khi đến thời gian chiếu
     * - ACTIVE -> ENDED khi kết thúc chiếu
     */
    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateScheduleStatuses() {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Bắt đầu cập nhật thủ công trạng thái lịch chiếu");

            // Cập nhật UPCOMING -> ACTIVE
            int upcomingToActiveCount = adminScheduleService.updateUpcomingToActiveSchedules();

            // Cập nhật ACTIVE -> ENDED
            int activeToEndedCount = adminScheduleService.updateExpiredScheduleStatuses();

            int totalUpdated = upcomingToActiveCount + activeToEndedCount;

            log.info("Cập nhật thủ công hoàn thành: {} UPCOMING->ACTIVE, {} ACTIVE->ENDED",
                    upcomingToActiveCount, activeToEndedCount);

            response.put("success", true);
            response.put("upcomingToActiveCount", upcomingToActiveCount);
            response.put("activeToEndedCount", activeToEndedCount);
            response.put("totalUpdated", totalUpdated);

            if (totalUpdated > 0) {
                response.put("message", String.format(
                        "Đã cập nhật thành công %d lịch chiếu (%d sắp chiếu → đang chiếu, %d đang chiếu → đã kết thúc)",
                        totalUpdated, upcomingToActiveCount, activeToEndedCount));
            } else {
                response.put("message", "Không có lịch chiếu nào cần cập nhật");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái lịch chiếu: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Hiển thị trang sửa lịch chiếu
     */
    @GetMapping("/edit/{scheduleId}")
    public String showEditSchedulePage(@PathVariable Integer scheduleId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            log.info("Hiển thị trang sửa lịch chiếu với ID: {}", scheduleId);

            // Lấy thông tin lịch chiếu
            Optional<ScreeningScheduleDto> scheduleOptional = adminScheduleService.getScreeningScheduleById(scheduleId);
            if (scheduleOptional.isEmpty()) {
                log.warn("Không tìm thấy lịch chiếu với ID: {}", scheduleId);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy lịch chiếu cần sửa");
                return "redirect:/admin/schedules/list";
            }

            ScreeningScheduleDto schedule = scheduleOptional.get();

            // Kiểm tra trạng thái - chỉ cho phép sửa lịch chiếu có trạng thái UPCOMING
            if (!"UPCOMING".equals(schedule.getStatus())) {
                log.warn("Không thể sửa lịch chiếu với trạng thái: {}", schedule.getStatus());
                redirectAttributes.addFlashAttribute("error",
                        "Chỉ có thể sửa lịch chiếu có trạng thái 'Sắp chiếu'");
                return "redirect:/admin/schedules/detail?date=" + schedule.getScreeningDate();
            }

            // Kiểm tra xem có booking nào chưa
            boolean hasBookings = bookingRepository.existsByScheduleId(scheduleId);
            if (hasBookings) {
                log.warn("Không thể sửa lịch chiếu đã có booking với ID: {}", scheduleId);
                redirectAttributes.addFlashAttribute("error",
                        "Không thể sửa lịch chiếu đã có người đặt vé");
                return "redirect:/admin/schedules/detail?date=" + schedule.getScreeningDate();
            }

            // Thêm dữ liệu vào model
            model.addAttribute("schedule", schedule);
            model.addAttribute("selectedBranchId", schedule.getBranchId());
            model.addAttribute("selectedBranchName", schedule.getBranchName());
            model.addAttribute("selectedDate", schedule.getScreeningDate().toString());
            model.addAttribute("selectedRoomId", schedule.getScreeningRoomId());
            model.addAttribute("selectedRoomName", schedule.getScreeningRoomName());
            model.addAttribute("selectedMovieId", schedule.getMovieId());
            model.addAttribute("selectedMovieName", schedule.getMovieName());
            model.addAttribute("selectedMovieDuration", schedule.getMovieDuration());

            // Lấy danh sách phòng chiếu của chi nhánh để hiển thị
            List<ScreeningRoom> rooms = roomRepository.findByBranchId(schedule.getBranchId());
            model.addAttribute("rooms", rooms);

            return "admin/admin_schedule_edit";

        } catch (Exception e) {
            log.error("Lỗi khi hiển thị trang sửa lịch chiếu: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải trang sửa lịch chiếu");
            return "redirect:/admin/schedules/list";
        }
    }

    /**
     * Xử lý cập nhật lịch chiếu
     */
    @PostMapping("/edit/{scheduleId}")
    public String updateSchedule(
            @PathVariable Integer scheduleId,
            @RequestParam String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) List<String> additionalStartTimes,
            @RequestParam(required = false) List<String> additionalEndTimes,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Cập nhật lịch chiếu với ID: {}, Start Time: {}", scheduleId, startTime);

            // Lấy thông tin lịch chiếu hiện tại
            Optional<ScreeningScheduleDto> scheduleOptional = adminScheduleService.getScreeningScheduleById(scheduleId);
            if (scheduleOptional.isEmpty()) {
                log.warn("Không tìm thấy lịch chiếu với ID: {}", scheduleId);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy lịch chiếu cần sửa");
                return "redirect:/admin/schedules/list";
            }

            ScreeningScheduleDto schedule = scheduleOptional.get();

            // Kiểm tra lại trạng thái và booking trước khi cập nhật
            if (!"UPCOMING".equals(schedule.getStatus())) {
                log.warn("Không thể sửa lịch chiếu với trạng thái: {}", schedule.getStatus());
                redirectAttributes.addFlashAttribute("error",
                        "Chỉ có thể sửa lịch chiếu có trạng thái 'Sắp chiếu'");
                return "redirect:/admin/schedules/detail?date=" + schedule.getScreeningDate();
            }

            boolean hasBookings = bookingRepository.existsByScheduleId(scheduleId);
            if (hasBookings) {
                log.warn("Không thể sửa lịch chiếu đã có booking với ID: {}", scheduleId);
                redirectAttributes.addFlashAttribute("error",
                        "Không thể sửa lịch chiếu đã có người đặt vé");
                return "redirect:/admin/schedules/detail?date=" + schedule.getScreeningDate();
            }

            // Cập nhật thời gian cho lịch chiếu chính
            schedule.setStartTime(LocalTime.parse(startTime));
            if (endTime != null && !endTime.isEmpty()) {
                schedule.setEndTime(LocalTime.parse(endTime));
            }

            // Lưu lịch chiếu đã cập nhật
            adminScheduleService.saveOrUpdateScreeningScheduleWithValidation(schedule);

            // Xử lý các lịch chiếu thêm nếu có
            if (additionalStartTimes != null && !additionalStartTimes.isEmpty()) {
                for (int i = 0; i < additionalStartTimes.size(); i++) {
                    String additionalStart = additionalStartTimes.get(i);
                    String additionalEnd = (additionalEndTimes != null && i < additionalEndTimes.size())
                            ? additionalEndTimes.get(i)
                            : null;

                    if (additionalStart != null && !additionalStart.isEmpty()) {
                        // Tạo lịch chiếu mới với thông tin tương tự nhưng thời gian khác
                        ScreeningScheduleDto newSchedule = new ScreeningScheduleDto();
                        newSchedule.setMovieId(schedule.getMovieId());
                        newSchedule.setScreeningRoomId(schedule.getScreeningRoomId());
                        newSchedule.setBranchId(schedule.getBranchId());
                        newSchedule.setScreeningDate(schedule.getScreeningDate());
                        newSchedule.setStartTime(LocalTime.parse(additionalStart));
                        if (additionalEnd != null && !additionalEnd.isEmpty()) {
                            newSchedule.setEndTime(LocalTime.parse(additionalEnd));
                        }
                        newSchedule.setStatus("UPCOMING");

                        adminScheduleService.saveOrUpdateScreeningScheduleWithValidation(newSchedule);
                    }
                }
            }

            log.info("Đã cập nhật lịch chiếu thành công với ID: {}", scheduleId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật lịch chiếu thành công!");

            return "redirect:/admin/schedules/detail?date=" + schedule.getScreeningDate();

        } catch (ScheduleConflictException e) {
            log.error("Xung đột lịch chiếu khi cập nhật: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Xung đột lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/edit/" + scheduleId;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật lịch chiếu: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật lịch chiếu");
            return "redirect:/admin/schedules/edit/" + scheduleId;
        }
    }
}
