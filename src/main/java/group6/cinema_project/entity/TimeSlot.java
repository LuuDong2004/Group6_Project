package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"showtimes"})
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "start_time", nullable = false, unique = true)
    private LocalTime startTime;
    
    @Column(name = "slot_name", length = 100)
    private String slotName;
    
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes;
} 