package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "cinema_halls")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"seats", "showtimes"})
public class CinemaHall {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "rows_count", nullable = false)
    private Integer rowsCount;
    
    @Column(name = "seats_per_row_max", nullable = false)
    private Integer seatsPerRowMax;
    
    @Column(name = "screen_type", length = 50)
    private String screenType;
    
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;
    
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes;
} 