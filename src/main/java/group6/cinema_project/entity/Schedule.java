package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Time;

import java.util.Date;

@Entity
@Table(name = "ScreeningSchedule")
@NoArgsConstructor
@Data
@Getter
@Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "MovieId", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "ScreeningRoomId")
    private ScreeningRoom screeningRoom;

    @ManyToOne
    @JoinColumn(name = "BranchId")
    private Branch branch;

    private Date screeningDate;

    private Time startTime;

    private Time endTime;
}
