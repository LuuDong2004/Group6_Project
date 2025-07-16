package group6.cinema_project.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ScreeningRoomDto {

    private Integer id;

    private String name;
    private int capacity; // sức chứa

    private String row;

    private BranchDto branch;
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private BranchDto branchDto;

    private List<SeatDto> seats;

    private List<ScreeningScheduleDto> screeningSchedules;
    private String branchName;
    private String branchAddress;
}
