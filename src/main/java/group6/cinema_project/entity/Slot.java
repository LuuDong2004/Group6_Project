package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

// đây là class slot chiếu được chia nhỏ từ slot trong 1 ngày
@Entity
@Table(name = "ScreeningTimeSlot")
@Data
@NoArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String startTime;
    private String description;
}
