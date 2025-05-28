package group6.cinema_project.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ScreeningSchedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "MovieId", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "screeningTimeSlotInDateId")
    private ScreeningTime screeningTimeSlot;
}
