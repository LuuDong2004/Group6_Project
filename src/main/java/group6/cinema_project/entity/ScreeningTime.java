package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// Đây là class thời gian chiếu trong ngày theo từng ngày
@Entity
@Table(name ="ScreeningTimeSlotInDate")
@Data
@NoArgsConstructor
public class ScreeningTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "screeningTimeSlotId")
    private Slot slot; // slotId

    @ManyToOne
    @JoinColumn(name = "DateId")
    private Date date;
    private String startTime;
}
