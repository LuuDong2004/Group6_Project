package group6.cinema_project.service.User;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScreeningScheduleDto;

import java.util.Date;
import java.util.List;

public interface IScheduleService {

    List<ScreeningScheduleDto> getScheduleByMovieId(Integer movieId);
    // Lấy lịch chiếu theo movieId và ngày cụ thể
    List<ScreeningScheduleDto> getScheduleByMovieIdAndDate(Integer movieId, Date screeningDate);

    // Lấy lịch chiếu theo movieId, branchId và khoảng ngày
    List<ScreeningScheduleDto> getScheduleByMovieIdAndBranchIdAndDate(Integer movieId, Integer branchId, Date startOfDay, Date endOfDay);

    // Lấy danh sách các rạp có lịch chiếu cho bộ phim
    List<BranchDto> getDistinctBranchesByMovieId(Integer movieId);

    // Lấy thông tin lịch chiếu theo ID
    ScreeningScheduleDto getScheduleById(Integer scheduleId);

    // Lấy danh sách các ngày có lịch chiếu
    List<Date> getAvailableScreeningDates(Integer movieId);
}
