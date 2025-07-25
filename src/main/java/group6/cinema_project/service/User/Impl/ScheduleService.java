package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.entity.Branch;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.repository.User.ScheduleRepository;
import group6.cinema_project.repository.User.SeatRepository;
import group6.cinema_project.repository.User.SeatReservationRepository;
import group6.cinema_project.service.User.IScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ScheduleService implements IScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatReservationRepository seatReservationRepository;

    public ScheduleService() {
    }

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<ScreeningScheduleDto> getScheduleByMovieId(Integer movieId) {
        List<ScreeningSchedule> schedules = scheduleRepository.findSchedulesByMovieId(movieId);

        // Lọc bỏ các lịch chiếu quá khứ
        Date currentDateTime = new Date();

        return schedules.stream()
                .filter(schedule -> !isScheduleInPast(schedule, currentDateTime))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScreeningScheduleDto> getScheduleByMovieIdAndDate(Integer movieId, Date screeningDate) {
        // Đảm bảo screeningDate được chuẩn hóa (chỉ lưu phần ngày)
        Date normalizedDate = normalizeDateToMidnight(screeningDate);
        Date currentDateTime = new Date();
        Date currentDate = normalizeDateToMidnight(currentDateTime);

        // Không cho phép truy vấn ngày quá khứ
        if (normalizedDate.before(currentDate)) {
            return new ArrayList<>(); // Trả về danh sách rỗng thay vì throw exception
        }

        List<ScreeningSchedule> schedules = scheduleRepository.findSchedulesByMovieIdAndDate(movieId, normalizedDate);

        // Nếu là ngày hiện tại, lọc bỏ các lịch chiếu có giờ bắt đầu đã qua
        if (isSameDay(normalizedDate, currentDate)) {
            schedules = schedules.stream()
                    .filter(schedule -> !isScheduleInPast(schedule, currentDateTime))
                    .collect(Collectors.toList());
        }

        return schedules.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScreeningScheduleDto> getScheduleByMovieIdAndBranchIdAndDate(Integer movieId, Integer branchId, Date startOfDay, Date endOfDay) {
        List<ScreeningSchedule> schedules;
        if (startOfDay == null || endOfDay == null) {
            // Lấy tất cả lịch chiếu của branch (không lọc ngày)
            schedules = scheduleRepository.findSchedulesByMovieIdAndBranchIdAndDate(movieId, branchId, null, null);
        } else {
            schedules = scheduleRepository.findSchedulesByMovieIdAndBranchIdAndDate(movieId, branchId, startOfDay, endOfDay);
        }
        Date currentDateTime = new Date();
        // Lọc bỏ các lịch chiếu quá khứ
        return schedules.stream()
                .filter(schedule -> !isScheduleInPast(schedule, currentDateTime))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchDto> getDistinctBranchesByMovieId(Integer movieId) {
        List<Object> branches = scheduleRepository.findDistinctBranchesByMovieId(movieId);
        return branches.stream()
                .filter(obj -> obj instanceof Branch)
                .map(obj -> modelMapper.map((Branch) obj, BranchDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ScreeningScheduleDto getScheduleById(Integer scheduleId) {
        ScreeningSchedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if (schedule == null) {
            return null;
        }

        // Kiểm tra nếu lịch chiếu đã qua
        Date currentDateTime = new Date();
        if (isScheduleInPast(schedule, currentDateTime)) {
            return null; // Không trả về lịch chiếu quá khứ
        }

        ScreeningScheduleDto dto = mapToDto(schedule);
        return dto;
    }

    @Override
    public List<Date> getAvailableScreeningDates(Integer movieId) {
        Date currentDate = normalizeDateToMidnight(new Date());
        List<Date> dates = scheduleRepository.findDistinctScreeningDatesByMovieIdFromDate(movieId, currentDate);

        // Chuẩn hóa các ngày (đã được lọc từ database)
        List<Date> normalizedDates = new ArrayList<>();
        for (Date date : dates) {
            Date normalizedDate = normalizeDateToMidnight(date);
            // Chỉ thêm ngày nếu chưa có trong danh sách
            if (!normalizedDates.contains(normalizedDate)) {
                normalizedDates.add(normalizedDate);
            }
        }

        // Sắp xếp lại các ngày
        Collections.sort(normalizedDates);

        return normalizedDates;
    }

    /**
     * Kiểm tra xem lịch chiếu có phải là quá khứ không
     */
    private boolean isScheduleInPast(ScreeningSchedule schedule, Date currentDateTime) {
        if (schedule.getScreeningDate() == null || schedule.getStartTime() == null) {
            return false;
        }

        // Kết hợp ngày chiếu và giờ bắt đầu
        Calendar scheduleCalendar = Calendar.getInstance();
        scheduleCalendar.setTime(schedule.getScreeningDate());

        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.setTime(schedule.getStartTime());

        // Đặt giờ, phút, giây từ startTime vào scheduleCalendar
        scheduleCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalendar.get(Calendar.HOUR_OF_DAY));
        scheduleCalendar.set(Calendar.MINUTE, startTimeCalendar.get(Calendar.MINUTE));
        scheduleCalendar.set(Calendar.SECOND, startTimeCalendar.get(Calendar.SECOND));
        scheduleCalendar.set(Calendar.MILLISECOND, 0);

        Date scheduleDateTime = scheduleCalendar.getTime();

        return scheduleDateTime.before(currentDateTime);
    }

    /**
     * Kiểm tra hai ngày có cùng một ngày không (bỏ qua giờ phút giây)
     */
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Helper method to normalize a date by setting the time to midnight (00:00:00.000)
     * This ensures we compare only the date part when working with dates
     */
    private Date normalizeDateToMidnight(Date date) {
        if (date == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private int getAvailableSeatsForSchedule(int scheduleId) {
        int totalSeats = seatRepository.findSeatsByRoomId(scheduleId).size();
        int reservedSeats = seatReservationRepository.findActiveReservationsByScheduleId(scheduleId).size();
        return totalSeats - reservedSeats;
    }

    public ScreeningScheduleDto mapToDto(ScreeningSchedule schedule) {
        ScreeningScheduleDto dto = modelMapper.map(schedule, ScreeningScheduleDto.class);
        if (schedule.getScreeningDate() != null) {
            dto.setScreeningDate(schedule.getScreeningDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            dto.setScreeningDateStr(dto.getScreeningDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (schedule.getStartTime() != null) {
            dto.setStartTime(schedule.getStartTime().toLocalTime());
            dto.setStartTimeStr(dto.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (schedule.getEndTime() != null) {
            dto.setEndTime(schedule.getEndTime().toLocalTime());
            dto.setEndTimeStr(dto.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        }
        dto.setAvailableSeats(getAvailableSeatsForSchedule(schedule.getId()));
        return dto;
    }
}