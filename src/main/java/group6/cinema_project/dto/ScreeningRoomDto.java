package group6.cinema_project.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
@NoArgsConstructor
public class ScreeningRoomDto {

    private Integer id;

    private String name;
    private int capacity; // sức chứa

    private int row;
    private String type;
    private BranchDto branch;
    private String status;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private BranchDto branchDto;

    private List<SeatDto> seats;

    private List<ScreeningScheduleDto> screeningSchedules;
    private String branchName;
    private String branchAddress;

    private int seatsPerRow; // số ghế mỗi hàng

}
