package group6.cinema_project.service;

import group6.cinema_project.dto.ScheduleDto;
import group6.cinema_project.entity.Schedule;
import group6.cinema_project.repository.ScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService implements IScheduleService{
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ScheduleService() {
    }

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }
    @Override
    public List<ScheduleDto> getScheduleByMovieId(Integer movieId) {
        List<Schedule> schedules = scheduleRepository.findSchedulesByMovieId(movieId);
        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDto.class))
                .collect(Collectors.toList());
    }
}
