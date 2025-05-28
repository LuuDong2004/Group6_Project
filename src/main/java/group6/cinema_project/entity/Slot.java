package group6.cinema_project.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

// đây là class slot chiếu được chia nhỏ từ slot trong 1 ngày
@Entity
@Table(name = "ScreeningTimeSlotInDate")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalTime startTime;
    private String description;
}
