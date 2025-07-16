package group6.cinema_project.controller;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.service.IMovieService;
import group6.cinema_project.service.IScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("schedule")
public class ScheduleController {
    @Autowired
    private IScheduleService scheduleService;
    @Autowired
    private IMovieService movieService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/movie/{movieId}/date")
    public String getSchedulesByMovieIdAndDate(
            @PathVariable(name = "movieId") Integer movieId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) Integer scheduleId,
            Model model) {

        try {
            Date currentDateTime = new Date(); // Thời gian hiện tại bao gồm cả giờ phút giây
            Date currentDate = normalizeDateToMidnight(currentDateTime); // Ngày hiện tại về 00:00:00

            // Nếu không có ngày được chọn, sử dụng ngày hiện tại
            if (date == null) {
                date = currentDate;
            }

            // Chuẩn hóa ngày (chỉ lấy phần ngày, bỏ giờ phút giây)
            Date normalizedDate = normalizeDateToMidnight(date);

             //Kiểm tra nếu ngày được chọn là ngày quá khứ
            if (normalizedDate.before(currentDate)) {
                model.addAttribute("error", "Không thể xem lịch chiếu của ngày đã qua. Vui lòng chọn ngày hôm nay hoặc các ngày tiếp theo.");
                model.addAttribute("movieId", movieId);
                return "error";
            }

            // Lấy thông tin phim
            List<MovieDto> movies = movieService.findMovieById(movieId);
            if (movies.isEmpty()) {
                model.addAttribute("error", "Movie not found");
                return "error";
            }

            // Lấy danh sách các ngày có lịch chiếu (7 ngày từ ngày hiện tại)
            // FIXED: Truyền normalizedDate để xác định ngày được chọn
            List<Map<String, Object>> dateList = generateDateList(7, normalizedDate);

            // Lấy danh sách ngày có lịch chiếu hợp lệ cho date picker
            List<Date> availableScreeningDates = scheduleService.getAvailableScreeningDates(movieId);
            model.addAttribute("availableScreeningDates", availableScreeningDates);
            // Thêm danh sách ngày dạng string yyyy-MM-dd cho JS
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<String> availableScreeningDateStrs = availableScreeningDates.stream()
                .map(sdf::format)
                .collect(Collectors.toList());
            model.addAttribute("availableScreeningDateStrs", availableScreeningDateStrs);

            // Lấy danh sách rạp có lịch chiếu cho phim vào ngày đã chọn
            List<BranchDto> branches = getBranchesForMovieAndDate(movieId, normalizedDate);

            // Lấy lịch chiếu cho ngày đã chọn (đã được lọc bỏ lịch chiếu quá khứ)
            List<ScreeningScheduleDto> schedules = scheduleService.getScheduleByMovieIdAndDate(movieId, normalizedDate);

            // Lọc bỏ các lịch chiếu có giờ bắt đầu đã qua (chỉ áp dụng cho ngày hiện tại)
            if (isSameDay(normalizedDate, currentDate)) {
                schedules = filterPastSchedules(schedules, currentDateTime);
            }

            // Kiểm tra nếu không có lịch chiếu nào
            if (schedules.isEmpty()) {
                String message = isSameDay(normalizedDate, currentDate)
                        ? "Không có lịch chiếu nào còn lại cho hôm nay."
                        : "Không có lịch chiếu nào cho ngày đã chọn.";
                model.addAttribute("noSchedulesMessage", message);
            }

            // Nhóm lịch chiếu theo rạp và phòng chiếu
            Map<Integer, Map<String, List<ScreeningScheduleDto>>> schedulesByBranchAndRoom = new HashMap<>();

            for (ScreeningScheduleDto schedule : schedules) {
                if (schedule.getBranch() == null) continue;

                int currBranchId = schedule.getBranch().getId();
                String roomName = schedule.getScreeningRoom() != null ?
                        schedule.getScreeningRoom().getName() : "Unknown Room";

                if (!schedulesByBranchAndRoom.containsKey(currBranchId)) {
                    schedulesByBranchAndRoom.put(currBranchId, new HashMap<>());
                }

                Map<String, List<ScreeningScheduleDto>> branchSchedules = schedulesByBranchAndRoom.get(currBranchId);

                if (!branchSchedules.containsKey(roomName)) {
                    branchSchedules.put(roomName, new ArrayList<>());
                }

                branchSchedules.get(roomName).add(schedule);
            }

            // Nếu có branchId được chọn, đánh dấu rạp đó là đã chọn
            if (branchId != null) {
                for (BranchDto branch : branches) {
                    if (branch.getId() == branchId) {
                        branch.setSelected(true);
                        break;
                    }
                }
            }

            // Nếu có scheduleId được chọn, đánh dấu lịch chiếu đó là đã chọn
            ScreeningScheduleDto selectedSchedule = null;
            if (scheduleId != null) {
                selectedSchedule = scheduleService.getScheduleById(scheduleId);
                // Kiểm tra nếu lịch chiếu đã chọn có phải là lịch chiếu quá khứ không
                if (selectedSchedule != null && isScheduleInPast(selectedSchedule, currentDateTime)) {
                    model.addAttribute("error", "Lịch chiếu đã chọn đã qua. Vui lòng chọn lịch chiếu khác.");
                    return "error";
                }
            }


            model.addAttribute("movie", movies);
            model.addAttribute("dateList", dateList);
            model.addAttribute("branches", branches);
            model.addAttribute("schedulesByBranchAndRoom", schedulesByBranchAndRoom);
            model.addAttribute("selectedDate", normalizedDate);
            model.addAttribute("selectedBranchId", branchId);
            model.addAttribute("selectedSchedule", selectedSchedule);
            model.addAttribute("movieId", movieId);
            model.addAttribute("formattedSelectedDate", new SimpleDateFormat("yyyy-MM-dd").format(normalizedDate));

            // Định dạng ngày cho URL
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("dateParam", dateFormat.format(normalizedDate));

            return "ticket-booking";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading movie schedules: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Lọc bỏ các lịch chiếu có thời gian bắt đầu đã qua
     */
    private List<ScreeningScheduleDto> filterPastSchedules(List<ScreeningScheduleDto> schedules, Date currentDateTime) {
        return schedules.stream()
                .filter(schedule -> !isScheduleInPast(schedule, currentDateTime))
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra xem lịch chiếu có phải là quá khứ không
     */
    private boolean isScheduleInPast(ScreeningScheduleDto schedule, Date currentDateTime) {
        if (schedule.getScreeningDate() == null || schedule.getStartTime() == null) {
            return false;
        }
        // Combine LocalDate and LocalTime to LocalDateTime
        java.time.LocalDateTime scheduleDateTime = java.time.LocalDateTime.of(
            schedule.getScreeningDate(), schedule.getStartTime()
        );
        java.time.LocalDateTime now = currentDateTime.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime();
        return scheduleDateTime.isBefore(now);
    }

    /**
     * Lấy danh sách rạp có lịch chiếu cho phim vào ngày cụ thể
     */
    private List<BranchDto> getBranchesForMovieAndDate(Integer movieId, Date date) {
        // Lấy tất cả lịch chiếu cho phim vào ngày cụ thể
        List<ScreeningScheduleDto> schedules = scheduleService.getScheduleByMovieIdAndDate(movieId, date);

        // Nếu là ngày hiện tại, lọc bỏ lịch chiếu quá khứ
        Date currentDateTime = new Date();
        Date currentDate = normalizeDateToMidnight(currentDateTime);

        if (isSameDay(date, currentDate)) {
            schedules = filterPastSchedules(schedules, currentDateTime);
        }

        // Trích xuất và loại bỏ trùng lặp các rạp
        Map<Integer, BranchDto> uniqueBranches = new HashMap<>();

        for (ScreeningScheduleDto schedule : schedules) {
            if (schedule.getBranch() != null) {
                uniqueBranches.put(schedule.getBranch().getId(), schedule.getBranch());
            }
        }

        return new ArrayList<>(uniqueBranches.values());
    }

    /**
     * FIXED: Tạo danh sách các ngày từ ngày hiện tại với selectedDate để xác định ngày được chọn
     */
    private List<Map<String, Object>> generateDateList(int numDays, Date selectedDate) {
        List<Map<String, Object>> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Đặt thời gian về 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < numDays; i++) {
            Map<String, Object> dateInfo = new HashMap<>();
            Date currentDate = calendar.getTime();

            dateInfo.put("date", currentDate);
            dateInfo.put("dayNum", calendar.get(Calendar.DAY_OF_MONTH));

            // FIXED: So sánh với selectedDate thay vì mặc định false
            dateInfo.put("selected", isSameDay(currentDate, selectedDate));

            // Đặt tên ngày
            if (i == 0) {
                dateInfo.put("dayName", "Today");
            } else if (i == 1) {
                dateInfo.put("dayName", "Tomorrow");
            } else {
                dateInfo.put("dayName", dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            }

            // Định dạng ngày cho URL
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateInfo.put("formattedDate", dateFormat.format(currentDate));

            dateList.add(dateInfo);

            // Tăng ngày lên 1
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateList;
    }

    /**
     * FIXED: Cải thiện hàm kiểm tra hai ngày có cùng một ngày không
     */
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return cal1.getTimeInMillis() == cal2.getTimeInMillis();
    }

    /**
     * Chuẩn hóa ngày bằng cách đặt giờ phút giây về 0
     */
    private Date normalizeDateToMidnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}