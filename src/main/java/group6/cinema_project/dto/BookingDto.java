package group6.cinema_project.dto;

import group6.cinema_project.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BookingDto {
    private int id;
    private UserDto user;
    private String code;
    private double amount;
    private String status;
    private LocalDate date;
    private Date expiryDate; // ngày hiệu lực của booking
    private String notes;
    private ScheduleDto schedule; // Thông tin lịch chiếu
    private List<SeatReservationDto> seats; // Danh sách ghế đã đặt
    private Map<String, Integer> foodItems; // Danh sách đồ ăn và số lượng
}
