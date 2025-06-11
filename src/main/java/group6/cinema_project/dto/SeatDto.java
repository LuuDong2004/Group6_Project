package group6.cinema_project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class SeatDto {
    private Integer id;
    private String name;
    private String row;
    private Integer status;
}
