package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import group6.cinema_project.entity.Enum.ScheduleStatus;

import java.sql.Time;

import java.util.Date;

@Entity
@Table(name = "ScreeningSchedule")
@NoArgsConstructor
@Data
public class ScreeningSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "movie_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screening_room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningRoom screeningRoom;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Branch branch;

    @Column(name = "screening_date")
    private Date screeningDate;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ScheduleStatus status;

}
