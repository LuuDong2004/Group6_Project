package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.entity.Seat;
import group6.cinema_project.repository.User.SeatRepository;
import group6.cinema_project.service.User.ISeatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService implements ISeatService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SeatDto> getSeatsByRoomId(Integer roomId) {
        List<Seat> seats = seatRepository.findSeatsByRoomId(roomId);
        return seats.stream()
                .map(seat -> modelMapper.map(seat, SeatDto.class))
                .collect(Collectors.toList());
    }



}