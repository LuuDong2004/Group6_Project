package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"moviePeople"})
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "biography", columnDefinition = "NVARCHAR(MAX)")
    private String biography;
    
    @Column(name = "photo", length = 500)
    private String photo;
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MoviePerson> moviePeople;
} 