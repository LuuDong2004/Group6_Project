package group6.cinema_project.dto;

import group6.cinema_project.entity.Branch;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Data
@NoArgsConstructor
public class ScreeningRoomDto {

    private int id;

    private String name;
    private int capacity; // sức chứa

    private String row;

    private BranchDto branch;
}
