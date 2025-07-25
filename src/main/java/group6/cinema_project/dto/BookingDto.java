package group6.cinema_project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class BookingDto {
    private Integer id;
    private UserDto user;
    private String code;
    private Integer amount;
    private String status;
    private LocalDate date;
    private Date expiryDate; // ngày hiệu lực của booking
    private String notes;
    private ScreeningScheduleDto schedule;
    private List<String> seatNames;
    private List<BookedFoodDto> foodList;
    private String voucherCode;
}
