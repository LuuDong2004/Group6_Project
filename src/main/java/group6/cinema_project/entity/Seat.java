package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uq_seat_in_hall", columnNames = {"hall_id", "row_char", "seat_num"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"showtimeSeatStatuses"})
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall hall;
    
    @Column(name = "row_char", nullable = false, length = 5)
    private String rowChar;
    
    @Column(name = "seat_num", nullable = false)
    private Integer seatNum;
    
    @Column(name = "type", length = 50)
    private String type = "Standard";
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShowtimeSeatStatus> showtimeSeatStatuses;
} 