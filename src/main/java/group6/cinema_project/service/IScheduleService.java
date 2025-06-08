package group6.cinema_project.service;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScheduleDto;

import java.util.Date;
import java.util.List;

public interface IScheduleService {

    List<ScheduleDto> getScheduleByMovieId(Integer movieId);
    // Lấy lịch chiếu theo movieId và ngày cụ thể
    List<ScheduleDto> getScheduleByMovieIdAndDate(Integer movieId, Date screeningDate);

    // Lấy lịch chiếu theo movieId, branchId và ngày
    List<ScheduleDto> getScheduleByMovieIdAndBranchIdAndDate(Integer movieId, Integer branchId, Date screeningDate);

    // Lấy danh sách các rạp có lịch chiếu cho bộ phim
    List<BranchDto> getDistinctBranchesByMovieId(Integer movieId);

    // Lấy thông tin lịch chiếu theo ID
    ScheduleDto getScheduleById(Integer scheduleId);

    // Lấy danh sách các ngày có lịch chiếu
    List<Date> getAvailableScreeningDates(Integer movieId);
}