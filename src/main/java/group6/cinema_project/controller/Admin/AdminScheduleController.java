
package group6.cinema_project.controller.Admin;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.MovieWithSchedulesDto;
import group6.cinema_project.dto.ScheduleGroupedByDateDto;
import group6.cinema_project.dto.ScheduleGroupedByRoomDto;
import group6.cinema_project.dto.ScheduleTimeSlotDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.exception.ScheduleConflictException;

import group6.cinema_project.service.Admin.IAdminBranchService;
import group6.cinema_project.service.Admin.IAdminMovieService;
import group6.cinema_project.service.Admin.IAdminRoomService;
import group6.cinema_project.service.Admin.IAdminScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.entity.ScreeningRoom;
import group6.cinema_project.entity.Branch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for handling admin schedule management operations.
 * Handles the display of movie schedules in the admin interface.
 */
@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleController {

    private final IAdminScheduleService movieScheduleService;
    private final IAdminMovieService movieService;
    private final IAdminRoomService screeningRoomService;
    private final IAdminBranchService branchService;

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
            // Load dropdown data for filters - convert MovieDto to MovieWithSchedulesDto
            List<MovieDto> movieDtos = movieService.getAllMovie();
            List<MovieWithSchedulesDto> moviesWithSchedules = convertMovieDtosToMoviesWithSchedules(movieDtos);
            model.addAttribute("movies", moviesWithSchedules);
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

        return "admin/admin_schedules_list";
    }

    /**
     * Hiển thị lịch chiếu chi tiết theo ngày
     * Endpoint này được gọi khi click vào một ngày trong calendar
     */
    @GetMapping("/list/date")
    public String listSchedulesByDate(Model model,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
            @RequestParam(value = "status", required = false) String status) {

        log.info("Loading schedules for date: {} with status filter: {}", selectedDate, status);

        try {
            // Nếu không có ngày được chọn, sử dụng ngày hiện tại
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }

            // Chuyển đổi LocalDate thành Date để tương thích với service
            Date startOfDay = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDay = Date.from(selectedDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

            // Lấy danh sách phim có lịch chiếu trong ngày
            List<MovieWithSchedulesDto> moviesWithSchedules = getMoviesWithSchedulesByDate(startOfDay, endOfDay,
                    status);

            // Format ngày để hiển thị
            String formattedDate = formatDateForDisplay(selectedDate);

            // Kiểm tra xem ngày có phải là quá khứ không
            boolean isPastDate = selectedDate.isBefore(LocalDate.now());

            // Thêm dữ liệu vào model
            model.addAttribute("movies", moviesWithSchedules);
            model.addAttribute("selectedDate", selectedDate);
            model.addAttribute("selectedDateFormatted", formattedDate);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("isPastDate", isPastDate);

            log.info("Successfully loaded {} movies with schedules for date: {}", moviesWithSchedules.size(),
                    selectedDate);

        } catch (Exception e) {
            log.error("Error loading schedules for date: " + selectedDate, e);
            model.addAttribute("error", "Lỗi khi tải lịch chiếu: " + e.getMessage());
            model.addAttribute("movies", Collections.emptyList());
            LocalDate finalSelectedDate = selectedDate != null ? selectedDate : LocalDate.now();
            model.addAttribute("selectedDate", finalSelectedDate);
            model.addAttribute("selectedDateFormatted", formatDateForDisplay(finalSelectedDate));
            model.addAttribute("isPastDate", finalSelectedDate.isBefore(LocalDate.now()));
        }

        return "admin/admin_schedules_list";
    }

    /**
     * Phương thức hỗ trợ để convert MovieDto thành MovieWithSchedulesDto với
     * schedules rỗng
     */

    private List<MovieWithSchedulesDto> convertMovieDtosToMoviesWithSchedules(List<MovieDto> movieDtos) {
        return movieDtos.stream().map(movieDto -> {
            MovieWithSchedulesDto movieWithSchedules = new MovieWithSchedulesDto();
            movieWithSchedules.setId(movieDto.getId());
            movieWithSchedules.setName(movieDto.getName());
            movieWithSchedules.setImage(movieDto.getImage());
            movieWithSchedules.setDuration(movieDto.getDuration());
            movieWithSchedules.setRating(movieDto.getRating());
            movieWithSchedules.setGenre(movieDto.getGenre());
            movieWithSchedules.setLanguage(movieDto.getLanguage());
            movieWithSchedules.setDescription(movieDto.getDescription());
            movieWithSchedules.setStatus(movieDto.getStatus());
            movieWithSchedules.setReleaseDate(movieDto.getReleaseDate());
            movieWithSchedules.setTrailer(movieDto.getTrailer());
            movieWithSchedules.setSchedules(new ArrayList<>()); // Danh sách schedules rỗng
            return movieWithSchedules;
        }).collect(Collectors.toList());
    }

    /**
     * Phương thức hỗ trợ để lấy danh sách phim với lịch chiếu theo ngày
     */
    private List<MovieWithSchedulesDto> getMoviesWithSchedulesByDate(Date startOfDay, Date endOfDay, String status) {
        List<MovieWithSchedulesDto> result = new ArrayList<>();

        try {
            // Lấy tất cả phim
            List<MovieDto> allMovies = movieService.getAllMovie();

            for (MovieDto movieDto : allMovies) {
                // Lấy lịch chiếu của phim theo ID
                List<ScreeningScheduleDto> schedulesDtos = movieScheduleService.getSchedulesByMovieId(movieDto.getId());

                // Lọc lịch chiếu theo ngày
                List<ScreeningScheduleDto> filteredSchedules = schedulesDtos.stream()
                        .filter(schedule -> {
                            LocalDate scheduleDate = schedule.getScreeningDate();
                            LocalDate targetDate = startOfDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return scheduleDate.equals(targetDate);
                        })
                        .map(schedule -> {
                            // Tự động set status cho lịch chiếu trong quá khứ
                            LocalDate scheduleDate = schedule.getScreeningDate();
                            LocalDate today = LocalDate.now();
                            if (scheduleDate.isBefore(today)) {
                                schedule.setStatus("ENDED");
                            }
                            return schedule;
                        })
                        .collect(Collectors.toList());

                // Lọc theo status nếu có
                if (status != null && !status.isEmpty() && !status.equals("all")) {
                    filteredSchedules = filteredSchedules.stream()
                            .filter(schedule -> {
                                String scheduleStatus = schedule.getStatus();
                                return scheduleStatus != null && scheduleStatus.equalsIgnoreCase(status);
                            })
                            .collect(Collectors.toList());
                }

                // Chỉ thêm phim nếu có lịch chiếu
                if (!filteredSchedules.isEmpty()) {
                    MovieWithSchedulesDto movieWithSchedules = new MovieWithSchedulesDto();
                    movieWithSchedules.setId(movieDto.getId());
                    movieWithSchedules.setName(movieDto.getName());
                    movieWithSchedules.setImage(movieDto.getImage());
                    movieWithSchedules.setDuration(movieDto.getDuration());
                    movieWithSchedules.setRating(movieDto.getRating());
                    movieWithSchedules.setGenre(movieDto.getGenre());
                    movieWithSchedules.setLanguage(movieDto.getLanguage());
                    movieWithSchedules.setDescription(movieDto.getDescription());
                    movieWithSchedules.setStatus(movieDto.getStatus());
                    movieWithSchedules.setReleaseDate(movieDto.getReleaseDate());
                    movieWithSchedules.setTrailer(movieDto.getTrailer());

                    // Chuyển đổi DTO thành Entity để tương thích với template
                    List<ScreeningSchedule> scheduleEntities = convertScheduleDtosToEntities(filteredSchedules);
                    movieWithSchedules.setSchedules(scheduleEntities);

                    result.add(movieWithSchedules);
                }
            }

            // Sắp xếp theo tên phim
            result.sort(Comparator.comparing(MovieWithSchedulesDto::getName));

        } catch (Exception e) {
            log.error("Error getting movies with schedules", e);
            throw new RuntimeException("Lỗi khi lấy danh sách phim và lịch chiếu", e);
        }

        return result;
    }

    /**
     * Chuyển đổi ScreeningScheduleDto thành ScreeningSchedule entity
     */
    private List<ScreeningSchedule> convertScheduleDtosToEntities(List<ScreeningScheduleDto> scheduleDtos) {
        return scheduleDtos.stream()
                .map(dto -> {
                    ScreeningSchedule entity = new ScreeningSchedule();
                    entity.setId(dto.getId());
                    entity.setScreeningDate(java.sql.Date.valueOf(dto.getScreeningDate()));
                    entity.setStartTime(dto.getStartTime() != null ? java.sql.Time.valueOf(dto.getStartTime()) : null);
                    entity.setEndTime(dto.getEndTime() != null ? java.sql.Time.valueOf(dto.getEndTime()) : null);
                    entity.setStatus(dto.getStatus());

                    // Tạo các entity liên quan nếu cần
                    if (dto.getScreeningRoomName() != null) {
                        ScreeningRoom room = new ScreeningRoom();
                        room.setName(dto.getScreeningRoomName());
                        entity.setScreeningRoom(room);
                    }

                    if (dto.getBranchName() != null) {
                        Branch branch = new Branch();
                        branch.setName(dto.getBranchName());
                        entity.setBranch(branch);
                    }

                    return entity;
                })
                .collect(Collectors.toList());
    }

    /**
     * Phương thức hỗ trợ để format ngày hiển thị
     */
    private String formatDateForDisplay(LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", new Locale("vi", "VN"));
            return date.format(formatter);
        } catch (Exception e) {
            log.warn("Error formatting date, using default format", e);
            return date.toString();
        }
    }

    /**
     * Default redirect to list page
     */
    @GetMapping
    public String defaultSchedulePage() {
        return "redirect:/admin/schedules/calendar";
    }

    /**
     * Display the add schedule form
     */
    @GetMapping("/add")
    public String showAddScheduleForm(Model model,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate) {
        log.info("Loading add schedule form with date: {}", selectedDate);

        try {
            // Create DTO for form binding
            ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();

            // Set ngày chiếu nếu có date parameter
            if (selectedDate != null) {
                scheduleDto.setScreeningDate(selectedDate);
            }

            // Luôn set trạng thái là "UPCOMING" (Sắp chiếu) cho lịch chiếu mới
            scheduleDto.setStatus("UPCOMING");

            model.addAttribute("schedule", scheduleDto);

            // Load dropdown data
            model.addAttribute("movies", movieService.getAllMovie());
            model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
            model.addAttribute("branches", branchService.getAllBranches());

            log.info("Successfully loaded add schedule form");
            return "admin/admin_schedules_add";

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
            return "admin/admin_schedules_add";
        }

        try {
            ScreeningScheduleDto savedSchedule = movieScheduleService
                    .saveOrUpdateScreeningScheduleWithValidation(scheduleDto);
            log.info("Successfully added schedule with ID: {}", savedSchedule.getId());

            redirectAttributes.addFlashAttribute("success", "Thêm lịch chiếu thành công!");

            // Redirect về trang list với ngày đã chọn để hiển thị lịch chiếu vừa thêm
            String redirectUrl = "redirect:/admin/schedules/list/date?date=" + scheduleDto.getScreeningDate();
            log.info("Redirecting to: {}", redirectUrl);
            return redirectUrl;

        } catch (ScheduleConflictException e) {
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
            return "admin/admin_schedules_add";
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

            // Redirect về trang list với ngày của lịch chiếu đã sửa
            String redirectUrl = "redirect:/admin/schedules/list/date?date=" + scheduleDto.getScreeningDate();
            log.info("Redirecting to: {}", redirectUrl);
            return redirectUrl;

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
            // Lấy thông tin lịch chiếu trước khi xóa để biết ngày
            Optional<ScreeningScheduleDto> scheduleOpt = movieScheduleService.getScreeningScheduleById(id);
            String redirectUrl = "redirect:/admin/schedules/list"; // default fallback

            if (scheduleOpt.isPresent()) {
                ScreeningScheduleDto schedule = scheduleOpt.get();
                redirectUrl = "redirect:/admin/schedules/list/date?date=" + schedule.getScreeningDate();
            }

            movieScheduleService.deleteScreeningSchedule(id);
            log.info("Successfully deleted schedule with ID: {}", id);

            redirectAttributes.addFlashAttribute("success", "Xóa lịch chiếu thành công!");
            return redirectUrl;

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

    @PostMapping("/add-batch")
    public String addBatchSchedules(
            @RequestParam("movieId") Integer movieId,
            @RequestParam("screeningDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate screeningDate,
            @RequestParam("status") String status,
            @RequestParam("timeSlots") String timeSlotsJson, // JSON array của các suất chiếu
            RedirectAttributes redirectAttributes) {

        log.info("Processing batch schedule request - movieId: {}, screeningDate: {}, status: {}",
                movieId, screeningDate, status);
        log.info("Received timeSlots JSON: {}", timeSlotsJson);

        try {
            // Kiểm tra dữ liệu đầu vào
            if (movieId == null || screeningDate == null || status == null || timeSlotsJson == null
                    || timeSlotsJson.trim().isEmpty()) {
                log.error("Missing required parameters - movieId: {}, screeningDate: {}, status: {}, timeSlotsJson: {}",
                        movieId, screeningDate, status, timeSlotsJson);
                redirectAttributes.addFlashAttribute("error", "Thiếu thông tin bắt buộc để tạo lịch chiếu");
                return "redirect:/admin/schedules/add";
            }

            // Tạo baseSchedule với thông tin chung
            ScreeningScheduleDto baseSchedule = new ScreeningScheduleDto();
            baseSchedule.setMovieId(movieId);
            baseSchedule.setScreeningDate(screeningDate);
            baseSchedule.setStatus(status);

            log.info("Created base schedule: {}", baseSchedule);

            // Chuyển đổi JSON string thành danh sách các suất chiếu
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> timeSlots = objectMapper.readValue(
                    timeSlotsJson,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            log.info("Parsed {} time slots from JSON", timeSlots.size());
            log.debug("Time slots details: {}", timeSlots);

            // Gọi service để lưu hàng loạt lịch chiếu
            List<ScreeningScheduleDto> savedSchedules = movieScheduleService.saveBatchSchedules(baseSchedule,
                    timeSlots);

            log.info("Successfully saved {} schedules", savedSchedules.size());

            redirectAttributes.addFlashAttribute("success",
                    String.format("Đã thêm thành công %d lịch chiếu cho phim!", savedSchedules.size()));

            return "redirect:/admin/schedules/list";

        } catch (ScheduleConflictException e) {
            log.error("Schedule conflict error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Phát hiện xung đột lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/add";
        } catch (Exception e) {
            log.error("Error adding batch schedules: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm lịch chiếu: " + e.getMessage());
            return "redirect:/admin/schedules/add";
        }
    }

    /**
     * Hiển thị trang lịch chiếu theo tháng (Calendar view)
     */
    @GetMapping("/calendar")
    public String showCalendarView(Model model,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        log.info("Loading calendar view - year: {}, month: {}", year, month);

        try {
            // Nếu không có tham số year/month, sử dụng tháng hiện tại
            LocalDate currentDate = LocalDate.now();
            int targetYear = (year != null) ? year : currentDate.getYear();
            int targetMonth = (month != null) ? month : currentDate.getMonthValue();

            // Tạo ngày đầu và cuối tháng để lấy dữ liệu
            LocalDate startOfMonth = LocalDate.of(targetYear, targetMonth, 1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

            log.info("Loading schedules for period: {} to {}", startOfMonth, endOfMonth);

            // Lấy tất cả lịch chiếu trong tháng
            List<ScreeningScheduleDto> allSchedules = movieScheduleService.getAllScreeningSchedulesForDisplay();

            // Filter theo tháng được chọn
            List<ScreeningScheduleDto> monthlySchedules = allSchedules.stream()
                    .filter(schedule -> {
                        LocalDate scheduleDate = schedule.getScreeningDate();
                        return scheduleDate != null &&
                                !scheduleDate.isBefore(startOfMonth) &&
                                !scheduleDate.isAfter(endOfMonth);
                    })
                    .collect(java.util.stream.Collectors.toList());

            // Format dữ liệu cho calendar frontend
            Map<String, Object> calendarData = formatSchedulesForCalendar(monthlySchedules);

            // Thêm dữ liệu vào model
            model.addAttribute("schedules", monthlySchedules);
            model.addAttribute("calendarData", calendarData);
            model.addAttribute("currentYear", targetYear);
            model.addAttribute("currentMonth", targetMonth);
            model.addAttribute("startOfMonth", startOfMonth);
            model.addAttribute("endOfMonth", endOfMonth);

            // Thống kê nhanh
            long playingCount = monthlySchedules.stream()
                    .filter(s -> "ACTIVE".equals(s.getStatus()))
                    .count();
            long comingSoonCount = monthlySchedules.stream()
                    .filter(s -> "UPCOMING".equals(s.getStatus()))
                    .count();
            long stoppedCount = monthlySchedules.stream()
                    .filter(s -> "ENDED".equals(s.getStatus()))
                    .count();

            model.addAttribute("totalSchedules", monthlySchedules.size());
            model.addAttribute("playingCount", playingCount);
            model.addAttribute("comingSoonCount", comingSoonCount);
            model.addAttribute("stoppedCount", stoppedCount);

            // Thêm tên tháng tiếng Việt
            String[] monthNames = {
                    "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            };
            model.addAttribute("currentMonthName", monthNames[targetMonth - 1]);

            log.info("Successfully loaded calendar view with {} schedules", monthlySchedules.size());
            return "admin/admin_schedule_calendar";

        } catch (Exception e) {
            log.error("Error loading calendar view", e);
            model.addAttribute("error", "Lỗi khi tải lịch chiếu: " + e.getMessage());
            model.addAttribute("schedules", java.util.Collections.emptyList());
            model.addAttribute("calendarData", new java.util.HashMap<>());
            model.addAttribute("currentYear", LocalDate.now().getYear());
            model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
            model.addAttribute("currentMonthName", "Tháng " + LocalDate.now().getMonthValue());
            model.addAttribute("totalSchedules", 0);
            model.addAttribute("playingCount", 0);
            model.addAttribute("comingSoonCount", 0);
            model.addAttribute("stoppedCount", 0);
            return "admin/admin_schedule_calendar";
        }
    }

    /**
     * Helper method để format dữ liệu lịch chiếu cho calendar frontend
     */
    private Map<String, Object> formatSchedulesForCalendar(List<ScreeningScheduleDto> schedules) {
        Map<String, Object> calendarData = new java.util.HashMap<>();

        // Nhóm lịch chiếu theo ngày
        Map<String, List<Map<String, Object>>> schedulesByDate = new java.util.HashMap<>();

        for (ScreeningScheduleDto schedule : schedules) {
            String dateKey = schedule.getScreeningDate().toString(); // Format: yyyy-MM-dd

            Map<String, Object> scheduleInfo = new java.util.HashMap<>();
            scheduleInfo.put("id", schedule.getId());
            scheduleInfo.put("time", schedule.getStartTime().toString());
            scheduleInfo.put("movie", schedule.getMovieName());
            scheduleInfo.put("room", schedule.getScreeningRoomName());
            scheduleInfo.put("status", mapStatusToFrontend(schedule.getStatus()));
            scheduleInfo.put("branchName", schedule.getBranchName());

            schedulesByDate.computeIfAbsent(dateKey, k -> new java.util.ArrayList<>()).add(scheduleInfo);
        }

        calendarData.put("schedulesByDate", schedulesByDate);
        calendarData.put("totalSchedules", schedules.size());

        return calendarData;
    }

    /**
     * API endpoint để search movies cho autocomplete
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public List<Map<String, Object>> searchMovies(@RequestParam("q") String query) {
        log.info("API request for movie search with query: {}", query);

        try {
            List<MovieDto> movies = movieService.getFilteredMoviesForDisplay(query, "name");

            return movies.stream()
                    .limit(10) // Giới hạn 10 kết quả
                    .map(movie -> {
                        Map<String, Object> movieData = new HashMap<>();
                        movieData.put("id", movie.getId());
                        movieData.put("name", movie.getName());
                        movieData.put("duration", movie.getDuration());
                        movieData.put("genre", movie.getGenre());
                        movieData.put("rating", movie.getRating());
                        return movieData;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching movies", e);
            return new ArrayList<>();
        }
    }

    /**
     * API endpoint để lấy thông tin chi tiết một phim
     */
    @GetMapping("/api/movies/{id}")
    @ResponseBody
    public Map<String, Object> getMovieById(@PathVariable("id") Integer id) {
        log.info("API request for movie details with ID: {}", id);

        try {
            Optional<MovieDto> movieOpt = movieService.getMovieById(id);

            if (movieOpt.isPresent()) {
                MovieDto movie = movieOpt.get();
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("id", movie.getId());
                movieData.put("name", movie.getName());
                movieData.put("duration", movie.getDuration());
                movieData.put("genre", movie.getGenre());
                movieData.put("rating", movie.getRating());
                return movieData;
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Không tìm thấy phim");
                return error;
            }

        } catch (Exception e) {
            log.error("Error getting movie details", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Lỗi khi lấy thông tin phim");
            return error;
        }
    }

    /**
     * API endpoint để kiểm tra xung đột lịch chiếu
     */
    @PostMapping("/api/check-conflict")
    @ResponseBody
    public Map<String, Object> checkScheduleConflict(@RequestBody Map<String, Object> conflictData) {
        log.info("API request for schedule conflict check: {}", conflictData);

        Map<String, Object> response = new HashMap<>();

        try {
            // Parse dữ liệu từ request
            String screeningDateStr = (String) conflictData.get("screeningDate");
            String startTimeStr = (String) conflictData.get("startTime");
            String endTimeStr = (String) conflictData.get("endTime");

            // Parse screeningRoomId an toàn từ String hoặc Integer
            Integer screeningRoomId = null;
            Object roomIdObj = conflictData.get("screeningRoomId");
            if (roomIdObj instanceof String) {
                try {
                    screeningRoomId = Integer.parseInt((String) roomIdObj);
                } catch (NumberFormatException e) {
                    log.warn("Invalid screeningRoomId format: {}", roomIdObj);
                }
            } else if (roomIdObj instanceof Integer) {
                screeningRoomId = (Integer) roomIdObj;
            }

            // Parse movieId an toàn từ String hoặc Integer
            Integer movieId = null;
            Object movieIdObj = conflictData.get("movieId");
            if (movieIdObj instanceof String) {
                try {
                    movieId = Integer.parseInt((String) movieIdObj);
                } catch (NumberFormatException e) {
                    log.warn("Invalid movieId format: {}", movieIdObj);
                }
            } else if (movieIdObj instanceof Integer) {
                movieId = (Integer) movieIdObj;
            }

            if (screeningDateStr == null || startTimeStr == null || endTimeStr == null || screeningRoomId == null
                    || movieId == null) {
                response.put("hasConflict", false);
                response.put("message", "Dữ liệu không đầy đủ để kiểm tra xung đột");
                return response;
            }

            // Parse excludeId nếu có (cho edit mode)
            Integer excludeId = null;
            Object excludeIdObj = conflictData.get("excludeId");
            if (excludeIdObj instanceof String) {
                try {
                    excludeId = Integer.parseInt((String) excludeIdObj);
                } catch (NumberFormatException e) {
                    log.warn("Invalid excludeId format: {}", excludeIdObj);
                }
            } else if (excludeIdObj instanceof Integer) {
                excludeId = (Integer) excludeIdObj;
            }

            // Tạo DTO để kiểm tra xung đột
            ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();
            scheduleDto.setId(excludeId); // Set ID để loại trừ khỏi conflict check
            scheduleDto.setMovieId(movieId); // Set movieId để validate
            scheduleDto.setScreeningDate(LocalDate.parse(screeningDateStr));
            scheduleDto.setStartTime(LocalTime.parse(startTimeStr));
            scheduleDto.setEndTime(LocalTime.parse(endTimeStr));
            scheduleDto.setScreeningRoomId(screeningRoomId);

            // Kiểm tra xung đột
            try {
                movieScheduleService.validateScheduleConflicts(scheduleDto);
                response.put("hasConflict", false);
                response.put("message", "Không có xung đột lịch chiếu");
            } catch (ScheduleConflictException e) {
                response.put("hasConflict", true);
                response.put("message", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Error checking schedule conflict", e);
            response.put("hasConflict", false);
            response.put("message", "Lỗi khi kiểm tra xung đột: " + e.getMessage());
        }

        return response;
    }

    /**
     * API endpoint để lấy dữ liệu lịch chiếu cho calendar (JSON response)
     */
    @GetMapping("/api/calendar-data")
    @ResponseBody
    public Map<String, Object> getCalendarData(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "status", required = false) String status) {

        log.info("API request for calendar data - year: {}, month: {}, status: {}", year, month, status);

        try {
            // Nếu không có tham số year/month, sử dụng tháng hiện tại
            LocalDate currentDate = LocalDate.now();
            int targetYear = (year != null) ? year : currentDate.getYear();
            int targetMonth = (month != null) ? month : currentDate.getMonthValue();

            // Tạo ngày đầu và cuối tháng
            LocalDate startOfMonth = LocalDate.of(targetYear, targetMonth, 1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

            // Lấy tất cả lịch chiếu
            List<ScreeningScheduleDto> allSchedules = movieScheduleService.getAllScreeningSchedulesForDisplay();

            // Filter theo tháng và status (nếu có)
            List<ScreeningScheduleDto> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        LocalDate scheduleDate = schedule.getScreeningDate();
                        boolean inMonth = scheduleDate != null &&
                                !scheduleDate.isBefore(startOfMonth) &&
                                !scheduleDate.isAfter(endOfMonth);

                        boolean statusMatch = (status == null || status.isEmpty() ||
                                status.equals("all") ||
                                status.equals(schedule.getStatus()));

                        return inMonth && statusMatch;
                    })
                    .collect(java.util.stream.Collectors.toList());

            // Nhóm lịch chiếu theo ngày
            Map<String, List<Map<String, Object>>> schedulesByDate = new java.util.HashMap<>();

            for (ScreeningScheduleDto schedule : filteredSchedules) {
                String dateKey = schedule.getScreeningDate().toString();

                Map<String, Object> scheduleData = new java.util.HashMap<>();
                scheduleData.put("id", schedule.getId());
                scheduleData.put("time", schedule.getStartTime().toString());
                scheduleData.put("movie", schedule.getMovieName());
                scheduleData.put("room", schedule.getScreeningRoomName());
                scheduleData.put("status", mapStatusToFrontend(schedule.getStatus()));
                scheduleData.put("branchName", schedule.getBranchName());

                schedulesByDate.computeIfAbsent(dateKey, k -> new java.util.ArrayList<>()).add(scheduleData);
            }

            // Tạo response
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("year", targetYear);
            response.put("month", targetMonth);
            response.put("schedules", schedulesByDate);
            response.put("totalCount", filteredSchedules.size());

            // Thống kê
            Map<String, Long> stats = new java.util.HashMap<>();
            stats.put("total", (long) filteredSchedules.size());
            stats.put("playing", filteredSchedules.stream().filter(s -> "ACTIVE".equals(s.getStatus())).count());
            stats.put("comingSoon", filteredSchedules.stream().filter(s -> "UPCOMING".equals(s.getStatus())).count());
            stats.put("stopped", filteredSchedules.stream().filter(s -> "ENDED".equals(s.getStatus())).count());
            response.put("stats", stats);

            return response;

        } catch (Exception e) {
            log.error("Error getting calendar data", e);
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("schedules", new java.util.HashMap<>());
            return errorResponse;
        }
    }

    /**
     * Helper method để map status từ backend sang frontend
     */
    private String mapStatusToFrontend(String backendStatus) {
        if (backendStatus == null)
            return "unknown";

        switch (backendStatus.toUpperCase()) {
            case "ACTIVE":
                return "playing";
            case "UPCOMING":
                return "coming-soon";
            case "ENDED":
                return "stopped";
            default:
                return "unknown";
        }
    }

}
