package group6.cinema_project.controller;

import group6.cinema_project.dto.SeatDto;
import group6.cinema_project.dto.TicketDto;
import group6.cinema_project.service.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequestMapping("api/seats")
public class SeatController {
    @Autowired
    private ISeatService seatService;

    @GetMapping("get/{sId}")
    public ResponseEntity<List<SeatDto>> getSeatsByScheduleId(@PathVariable(name = "sId") Integer scheduleId) {
        List<SeatDto> seats = seatService.getSeatsByScheduleId(scheduleId);
        return ResponseEntity.ok(seats);
    }
}
