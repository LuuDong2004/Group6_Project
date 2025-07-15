package group6.cinema_project.controller;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScheduleGroupedByDateDto;
import group6.cinema_project.dto.ScheduleGroupedByRoomDto;
import group6.cinema_project.dto.ScheduleTimeSlotDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.exception.ScheduleConflictException;
import group6.cinema_project.service.IMovieScheduleService;
import group6.cinema_project.service.IMovieService;
import group6.cinema_project.service.IScreeningRoomService;
import group6.cinema_project.service.IBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for handling admin schedule management operations.
 * Handles the display of movie schedules in the admin interface.
 */
@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleController {

    private final IMovieScheduleService movieScheduleService;
    private final IMovieService movieService;
    private final IScreeningRoomService screeningRoomService;
    private final IBranchService branchService;

    /**
     * Display the schedule list page with optional filtering
     */

    @GetMapping("/list")
    public String listSchedules(Model model,
            @RequestParam(value = "movieId", required = false) Integer movieId,
            @RequestParam(value = "screeningDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate screeningDate,
            @RequestParam(value = "screeningRoomId", required = false) Integer screeningRoomId) {

        log.info("Loading schedule list with filters - movieId: {}, screeningDate: {}, screeningRoomId: {}",
                movieId, screeningDate, screeningRoomId);

        try {
            // Load schedule data
            List<ScreeningScheduleDto> schedules;

            // If any filter parameters are provided, use filtered search; otherwise get all
            // schedules
            if (movieId != null || screeningDate != null || screeningRoomId != null) {
                log.info("Using filtered search for schedules");
                schedules = movieScheduleService.getFilteredScreeningSchedulesForDisplay(
                        movieId, screeningDate, screeningRoomId);
            } else {
                log.info("Loading all schedules");
                schedules = movieScheduleService.getAllScreeningSchedulesForDisplay();
            }

            log.info("Successfully loaded {} schedules", schedules.size());

            // Add schedules to model
            model.addAttribute("schedules", schedules);

            // Add success message if filtering was applied
            if (movieId != null || screeningDate != null || screeningRoomId != null) {
                model.addAttribute("message", "Tìm thấy " + schedules.size() + " lịch chiếu phù hợp");
            }

        } catch (Exception e) {
            // Handle schedule loading errors
            log.error("Error loading schedule data", e);
            model.addAttribute("error", "Lỗi khi tải dữ liệu lịch chiếu: " + e.getMessage());
            model.addAttribute("schedules", java.util.Collections.emptyList());
        }

        try {
            // Load dropdown data for filters
            model.addAttribute("movies", movieService.getAllMovie());
        } catch (Exception e) {
            model.addAttribute("movies", java.util.Collections.emptyList());
            if (!model.containsAttribute("error")) {
                model.addAttribute("error", "Lỗi khi tải danh sách phim: " + e.getMessage());
            }
        }

        try {
            model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
        } catch (Exception e) {
            model.addAttribute("screeningRooms", java.util.Collections.emptyList());
            if (!model.containsAttribute("error")) {
                model.addAttribute("error", "Lỗi khi tải danh sách phòng chiếu: " + e.getMessage());
            }
        }

        // Add current filter values to model to maintain state (always safe to add)
        model.addAttribute("selectedMovieId", movieId);
        model.addAttribute("selectedScreeningDate", screeningDate);
        model.addAttribute("selectedScreeningRoomId", screeningRoomId);

        return "admin/admin_schedule_list";
    }

    /**
     * Default redirect to list page
     */
    @GetMapping
    public String defaultSchedulePage() {
        return "redirect:/admin/schedules/list";
    }

    /**
     * Test endpoint to verify the service works without Invoice issues
     */
    @GetMapping("/test")
    public String testSchedules(Model model) {
        try {
            List<ScreeningScheduleDto> schedules = movieScheduleService.getAllScreeningSchedulesForDisplay();
            model.addAttribute("schedules", schedules);
            model.addAttribute("message", "Successfully loaded " + schedules.size() + " schedules");

            // Add empty collections for dropdowns to prevent template errors
            model.addAttribute("movies", java.util.Collections.emptyList());
            model.addAttribute("screeningRooms", java.util.Collections.emptyList());
            model.addAttribute("selectedMovieId", null);
            model.addAttribute("selectedScreeningDate", null);
            model.addAttribute("selectedScreeningRoomId", null);

            return "admin/admin_schedule_list";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading schedules: " + e.getMessage());
            model.addAttribute("schedules", java.util.Collections.emptyList());
            model.addAttribute("movies", java.util.Collections.emptyList());
            model.addAttribute("screeningRooms", java.util.Collections.emptyList());
            model.addAttribute("selectedMovieId", null);
            model.addAttribute("selectedScreeningDate", null);
            model.addAttribute("selectedScreeningRoomId", null);
            return "admin/admin_schedule_list";
        }
    }

    /**
     * Debug endpoint to check what data is available
     */
    @GetMapping("/debug")
    public String debugSchedules(Model model) {
        StringBuilder debugInfo = new StringBuilder();

        try {
            List<ScreeningScheduleDto> schedules = movieScheduleService.getAllScreeningSchedulesForDisplay();
            debugInfo.append("Schedules loaded: ").append(schedules.size()).append("<br>");

            if (!schedules.isEmpty()) {
                ScreeningScheduleDto firstSchedule = schedules.get(0);
                debugInfo.append("First schedule ID: ").append(firstSchedule.getId()).append("<br>");
                debugInfo.append("Movie name: ").append(firstSchedule.getMovieName()).append("<br>");
                debugInfo.append("Room name: ").append(firstSchedule.getScreeningRoomName()).append("<br>");
            }

            model.addAttribute("schedules", schedules);
        } catch (Exception e) {
            debugInfo.append("Schedule error: ").append(e.getMessage()).append("<br>");
            model.addAttribute("schedules", java.util.Collections.emptyList());
        }

        try {
            var movies = movieService.getAllMovie();
            debugInfo.append("Movies loaded: ").append(movies.size()).append("<br>");
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            debugInfo.append("Movies error: ").append(e.getMessage()).append("<br>");
            model.addAttribute("movies", java.util.Collections.emptyList());
        }

        try {
            var rooms = screeningRoomService.getAllScreeningRooms();
            debugInfo.append("Screening rooms loaded: ").append(rooms.size()).append("<br>");
            model.addAttribute("screeningRooms", rooms);
        } catch (Exception e) {
            debugInfo.append("Screening rooms error: ").append(e.getMessage()).append("<br>");
            model.addAttribute("screeningRooms", java.util.Collections.emptyList());
        }

        model.addAttribute("message", debugInfo.toString());
        model.addAttribute("selectedMovieId", null);
        model.addAttribute("selectedScreeningDate", null);
        model.addAttribute("selectedScreeningRoomId", null);

        return "admin/admin_schedule_list";
    }

    /**
     * Display the add schedule form
     */
    @GetMapping("/add")
    public String showAddScheduleForm(Model model) {
        log.info("Loading add schedule form");

        try {
            // Add empty DTO for form binding
            model.addAttribute("schedule", new ScreeningScheduleDto());

            // Load dropdown data
            model.addAttribute("movies", movieService.getAllMovie());
            model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
            model.addAttribute("branches", branchService.getAllBranches());

            log.info("Successfully loaded add schedule form");
            return "admin/admin_schedule_add";

        } catch (Exception e) {
            log.error("Error loading add schedule form", e);
            model.addAttribute("error", "Lỗi khi tải form thêm lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/list";
        }
    }

    @PostMapping("/add")
    public String addSchedule(@Valid @ModelAttribute("schedule") ScreeningScheduleDto scheduleDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Processing add schedule request for movie ID: {}", scheduleDto.getMovieId());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add schedule form");
            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception e) {
                log.error("Error reloading dropdown data", e);
            }
            return "admin/admin_schedule_add";
        }

        try {
            ScreeningScheduleDto savedSchedule = movieScheduleService
                    .saveOrUpdateScreeningScheduleWithValidation(scheduleDto);
            log.info("Successfully added schedule with ID: {}", savedSchedule.getId());

            redirectAttributes.addFlashAttribute("success", "Thêm lịch chiếu thành công!");
            return "redirect:/admin/schedules/list";

        } catch (group6.cinema_project.exception.ScheduleConflictException e) {
            log.warn("Schedule conflict detected: {}", e.getDetailedMessage());

            // Add specific conflict error to binding result
            bindingResult.rejectValue("startTime", "error.schedule.conflict", e.getDetailedMessage());

            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception ex) {
                log.error("Error reloading dropdown data", ex);
            }
            return "admin/admin_schedule_add";

        } catch (Exception e) {
            log.error("Error adding schedule", e);
            model.addAttribute("error", "Lỗi khi thêm lịch chiếu: " + e.getMessage());

            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception ex) {
                log.error("Error reloading dropdown data", ex);
            }
            return "admin/admin_schedule_add";
        }
    }

    /**
     * Display the edit schedule form
     */
    @GetMapping("/edit/{id}")
    public String showEditScheduleForm(@PathVariable("id") Integer id, Model model) {
        log.info("Loading edit schedule form for ID: {}", id);

        try {
            Optional<ScreeningScheduleDto> scheduleOpt = movieScheduleService.getScreeningScheduleById(id);
            if (scheduleOpt.isEmpty()) {
                log.warn("Schedule not found with ID: {}", id);
                model.addAttribute("error", "Không tìm thấy lịch chiếu với ID: " + id);
                return "redirect:/admin/schedules/list";
            }

            model.addAttribute("schedule", scheduleOpt.get());
            model.addAttribute("movies", movieService.getAllMovie());
            model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
            model.addAttribute("branches", branchService.getAllBranches());

            log.info("Successfully loaded edit schedule form for ID: {}", id);
            return "admin/admin_schedule_edit";

        } catch (Exception e) {
            log.error("Error loading edit schedule form for ID: {}", id, e);
            model.addAttribute("error", "Lỗi khi tải form chỉnh sửa lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/list";
        }
    }

    /**
     * Handle edit schedule form submission
     */
    @PostMapping("/edit/{id}")
    public String editSchedule(@PathVariable("id") Integer id,
            @Valid @ModelAttribute("schedule") ScreeningScheduleDto scheduleDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Processing edit schedule request for ID: {}", id);

        // Set the ID to ensure we're updating the correct record
        scheduleDto.setId(id);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in edit schedule form for ID: {}", id);
            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception e) {
                log.error("Error reloading dropdown data", e);
            }
            return "admin/admin_schedule_edit";
        }

        try {
            ScreeningScheduleDto updatedSchedule = movieScheduleService
                    .saveOrUpdateScreeningScheduleWithValidation(scheduleDto);
            log.info("Successfully updated schedule with ID: {}", updatedSchedule.getId());

            redirectAttributes.addFlashAttribute("success", "Cập nhật lịch chiếu thành công!");
            return "redirect:/admin/schedules/list";

        } catch (IllegalStateException e) {
            log.warn("Cannot update a movie schedule is currently playing");
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedules/list";
        } catch (group6.cinema_project.exception.ScheduleConflictException e) {
            log.warn("Schedule conflict detected during update: {}", e.getDetailedMessage());

            // Add specific conflict error to binding result
            bindingResult.rejectValue("startTime", "error.schedule.conflict", e.getDetailedMessage());

            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception ex) {
                log.error("Error reloading dropdown data", ex);
            }
            return "admin/admin_schedule_edit";

        } catch (Exception e) {
            log.error("Error updating schedule with ID: {}", id, e);
            model.addAttribute("error", "Lỗi khi cập nhật lịch chiếu: " + e.getMessage());

            // Reload dropdown data for form
            try {
                model.addAttribute("movies", movieService.getAllMovie());
                model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
                model.addAttribute("branches", branchService.getAllBranches());
            } catch (Exception ex) {
                log.error("Error reloading dropdown data", ex);
            }
            return "admin/admin_schedule_edit";
        }
    }

    /**
     * Handle delete schedule request
     */
    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        log.info("Processing delete schedule request for ID: {}", id);

        try {
            movieScheduleService.deleteScreeningSchedule(id);
            log.info("Successfully deleted schedule with ID: {}", id);

            redirectAttributes.addFlashAttribute("success", "Xóa lịch chiếu thành công!");
            return "redirect:/admin/schedules/list";

        } catch (IllegalStateException e) {
            log.warn("Cannot delete a movie schedule is currently playing");
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedules/list";
        } catch (Exception e) {
            log.error("Error deleting schedule with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/list";
        }
    }

    /**
     * Display movies currently playing (ACTIVE status)
     */
    @GetMapping("/list/playing")
    public String listPlayingMovies(Model model) {
        log.info("Loading currently playing movies");

        try {
            movieScheduleService.updateUpcomingToActiveSchedules();
            movieScheduleService.updateExpiredScheduleStatuses();

            List<MovieDto> movies = movieScheduleService.getMoviesByScheduleStatus("ACTIVE");
            model.addAttribute("movies", movies);
            model.addAttribute("currentTab", "PLAYING");
            model.addAttribute("tabTitle", "Đang chiếu");

            log.info("Successfully loaded {} currently playing movies", movies.size());
            return "admin/admin_schedules_list_playing";

        } catch (Exception e) {
            log.error("Error loading currently playing movies", e);
            model.addAttribute("error", "Lỗi khi tải danh sách phim đang chiếu: " + e.getMessage());
            model.addAttribute("movies", java.util.Collections.emptyList());
            return "admin/admin_schedules_list_playing";
        }
    }

    /**
     * Display movies coming soon (UPCOMING status)
     */
    @GetMapping("/list/comingsoon")
    public String listComingSoonMovies(Model model) {
        log.info("Loading coming soon movies");

        try {
            List<MovieDto> movies = movieScheduleService.getMoviesByScheduleStatus("UPCOMING");
            model.addAttribute("movies", movies);
            model.addAttribute("currentTab", "comingsoon");
            model.addAttribute("tabTitle", "Sắp chiếu");

            log.info("Successfully loaded {} coming soon movies", movies.size());
            return "admin/admin_schedules_list_comingsoon";

        } catch (Exception e) {
            log.error("Error loading coming soon movies", e);
            model.addAttribute("error", "Lỗi khi tải danh sách phim sắp chiếu: " + e.getMessage());
            model.addAttribute("movies", java.util.Collections.emptyList());
            return "admin/admin_schedules_list_comingsoon";
        }
    }

    /**
     * Display movies that have stopped showing (ENDED status)
     */
    @GetMapping("/list/stopped")
    public String listStoppedMovies(Model model) {
        log.info("Loading stopped showing movies");

        try {
            List<MovieDto> movies = movieScheduleService.getMoviesByScheduleStatus("ENDED");
            model.addAttribute("movies", movies);
            model.addAttribute("currentTab", "stopped");
            model.addAttribute("tabTitle", "Ngừng chiếu");

            log.info("Successfully loaded {} stopped showing movies", movies.size());
            return "admin/admin_schedules_list_stopped";

        } catch (Exception e) {
            log.error("Error loading stopped showing movies", e);
            model.addAttribute("error", "Lỗi khi tải danh sách phim ngừng chiếu: " + e.getMessage());
            model.addAttribute("movies", java.util.Collections.emptyList());
            return "admin/admin_schedules_list_stopped";
        }
    }

    /**
     * Display detailed schedules for a specific movie
     */
    @GetMapping("/detail/{movieId}")
    public String showMovieScheduleDetail(@PathVariable("movieId") Integer movieId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "backUrl", required = false) String backUrl,
            Model model) {

        log.info("Đang tải chi tiết lịch chiếu cho phim ID: {} với bộ lọc trạng thái: {}", movieId, status);

        // Sử dụng dữ liệu đã được nhóm sẵn để giảm độ phức tạp frontend
        List<ScheduleGroupedByDateDto> groupedSchedules;
        List<ScreeningScheduleDto> allSchedules; // Để lấy thông tin phim
        String statusText = "Tất cả";
        String defaultBackUrl = "/admin/schedules/list/playing";

        try {
            // Lấy dữ liệu đã được nhóm sẵn
            groupedSchedules = movieScheduleService.getSchedulesByMovieIdGrouped(movieId);

            // Lấy tất cả schedules để có thông tin phim (có thể optimize sau)
            allSchedules = movieScheduleService.getSchedulesByMovieId(movieId);

            // Xử lý filter theo status nếu có
            if (status != null && !status.trim().isEmpty()) {
                switch (status.toUpperCase()) {
                    case "UPCOMING":
                        statusText = "Sắp chiếu";
                        defaultBackUrl = "/admin/schedules/list/comingsoon";
                        break;
                    case "ACTIVE":
                    case "PLAYING":
                        statusText = "Đang chiếu";
                        defaultBackUrl = "/admin/schedules/list/playing";
                        break;
                    case "ENDED":
                        statusText = "Đã kết thúc";
                        defaultBackUrl = "/admin/schedules/list/stopped";
                        break;
                    default:
                        statusText = "Tất cả";
                        break;
                }

                // Filter grouped schedules by status
                groupedSchedules = filterSchedulesByStatus(groupedSchedules, status);
            }

            log.info("Tìm thấy {} ngày có lịch chiếu với status: {}", groupedSchedules.size(), statusText);

            if (groupedSchedules.isEmpty() || allSchedules.isEmpty()) {
                String errorMessage = status != null
                        ? "Không tìm thấy lịch chiếu " + statusText.toLowerCase() + " cho phim này"
                        : "Không tìm thấy lịch chiếu cho phim này";
                log.warn(errorMessage);
                model.addAttribute("error", errorMessage);
                return "redirect:" + (backUrl != null ? backUrl : defaultBackUrl);
            }

            // Lấy thông tin phim từ lịch chiếu đầu tiên
            ScreeningScheduleDto firstSchedule = allSchedules.get(0);
            model.addAttribute("movieName", firstSchedule.getMovieName());
            model.addAttribute("movieImage", firstSchedule.getMovieImage());
            model.addAttribute("movieId", movieId);
            model.addAttribute("groupedSchedules", groupedSchedules); // Dữ liệu đã nhóm
            model.addAttribute("statusText", statusText);
            model.addAttribute("currentStatus", status);
            model.addAttribute("backUrl", backUrl != null ? backUrl : defaultBackUrl);

            // Tính tổng số lịch chiếu để hiển thị
            int totalSchedules = groupedSchedules.stream()
                    .mapToInt(ScheduleGroupedByDateDto::getTotalTimeSlots)
                    .sum();

            log.info("Đã tải thành công {} lịch chiếu cho phim: {} ({})",
                    totalSchedules, firstSchedule.getMovieName(), statusText);
            return "admin/admin_detail_schedules_list";

        } catch (Exception e) {
            log.error("Lỗi khi tải chi tiết lịch chiếu cho phim ID: {} với trạng thái: {}", movieId, status, e);
            model.addAttribute("error", "Lỗi khi tải chi tiết lịch chiếu: " + e.getMessage());
            return "redirect:" + (backUrl != null ? backUrl : defaultBackUrl);
        }
    }

    private List<ScheduleGroupedByDateDto> filterSchedulesByStatus(
            List<ScheduleGroupedByDateDto> schedules, String status) {

        return schedules.stream()
                .map(dateGroup -> filterDateGroup(dateGroup, status))
                .filter(dateGroup -> !dateGroup.getRooms().isEmpty())
                .collect(Collectors.toList());
    }

    private ScheduleGroupedByDateDto filterDateGroup(ScheduleGroupedByDateDto dateGroup, String status) {
        List<ScheduleGroupedByRoomDto> filteredRooms = dateGroup.getRooms().stream()
                .map(roomGroup -> filterRoomGroup(roomGroup, status))
                .filter(roomGroup -> !roomGroup.getTimeSlots().isEmpty())
                .collect(Collectors.toList());

        return new ScheduleGroupedByDateDto(dateGroup.getDate(), filteredRooms);
    }

    private ScheduleGroupedByRoomDto filterRoomGroup(ScheduleGroupedByRoomDto roomGroup, String status) {
        List<ScheduleTimeSlotDto> filteredTimeSlots = roomGroup.getTimeSlots().stream()
                .filter(timeSlot -> status.equalsIgnoreCase(timeSlot.getStatus()))
                .collect(Collectors.toList());

        return new ScheduleGroupedByRoomDto(
                roomGroup.getRoomName(),
                roomGroup.getBranchName(),
                filteredTimeSlots);
    }

    @PostMapping("/update-statuses")
    public String updateScheduleStatuses(RedirectAttributes redirectAttributes) {
        log.info("Processing request to update movie schedule statuses");

        try {
            int upcomingToActiveCount = movieScheduleService.updateUpcomingToActiveSchedules();
            int activeToEndedCount = movieScheduleService.updateExpiredScheduleStatuses();
            int totalUpdated = upcomingToActiveCount + activeToEndedCount;

            if (totalUpdated > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Đã cập nhật trạng thái cho " + totalUpdated + " lịch chiếu (" +
                                upcomingToActiveCount + " từ sắp chiếu thành đang chiếu, " +
                                activeToEndedCount + " từ đang chiếu thành đã kết thúc)");
            } else {
                redirectAttributes.addFlashAttribute("message", "Không có lịch chiếu nào cần cập nhật trạng thái");
            }

            return "redirect:/admin/schedules/list";

        } catch (Exception e) {
            log.error("Error updating schedule statuses", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/list";
        }
    }

    @PostMapping("/add-batch")
    public String addBatchSchedules(
            @RequestParam("movieId") Integer movieId,
            @RequestParam("screeningDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate screeningDate,
            @RequestParam("price") BigDecimal price,
            @RequestParam("status") String status,
            @RequestParam("timeSlots") String timeSlotsJson, // JSON array của các suất chiếu
            RedirectAttributes redirectAttributes) {

        try {
            // Tạo baseSchedule với thông tin chung
            ScreeningScheduleDto baseSchedule = new ScreeningScheduleDto();
            baseSchedule.setMovieId(movieId);
            baseSchedule.setScreeningDate(screeningDate);
            baseSchedule.setPrice(price);
            baseSchedule.setStatus(status);

            // Chuyển đổi JSON string thành danh sách các suất chiếu
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> timeSlots = objectMapper.readValue(
                    timeSlotsJson,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            // Gọi service để lưu hàng loạt lịch chiếu
            List<ScreeningScheduleDto> savedSchedules = movieScheduleService.saveBatchSchedules(baseSchedule,
                    timeSlots);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Đã thêm thành công %d lịch chiếu cho phim!", savedSchedules.size()));

            return "redirect:/admin/schedules/list";

        } catch (ScheduleConflictException e) {
            redirectAttributes.addFlashAttribute("error", "Phát hiện xung đột lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/add";
        }
    }

}
