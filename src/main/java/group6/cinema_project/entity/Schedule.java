package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ScreeningSchedule")
@NoArgsConstructor
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
//    private LocalDate startDate;
//    private LocalTime startTime;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "MovieId", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screeningTimeSlotInDateId")
    private ScreeningTime screeningTimeSlot;

    @ManyToOne
    @JoinColumn(name = "ScreeningRoomId")
    private ScreeningRoom screeningRoom;

    @ManyToOne
    @JoinColumn(name = "BranchId")
    private Branch branch;



}
