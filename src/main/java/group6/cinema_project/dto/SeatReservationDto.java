package group6.cinema_project.dto;

import lombok.Data;

@Data
public class SeatReservationDto {
    private Integer seatId;
    private String seatName;
    private String row;
    private String status;
} 