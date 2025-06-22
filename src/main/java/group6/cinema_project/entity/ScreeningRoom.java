package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "ScreeningRoom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "branch_id")
    private Integer branchId;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Branch branch;
    
    @OneToMany(mappedBy = "screeningRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Seat> seats;
    
    @OneToMany(mappedBy = "screeningRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ScreeningSchedule> screeningSchedules;
}