package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "movie_people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MoviePersonId.class)
@ToString
public class MoviePerson {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    
    @Id
    @Column(name = "role_type", nullable = false, length = 100)
    private String roleType;
} 