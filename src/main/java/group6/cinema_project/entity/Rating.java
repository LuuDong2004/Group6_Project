package group6.cinema_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "movies" })
@EqualsAndHashCode(exclude = { "movies" })
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", nullable = false, length = 10, unique = true)
    private String code; // G, K, T13, T16, T18, C

    @Column(name = "description", length = 255)
    private String description; // Mô tả rating

    // Quan hệ One-to-Many với Movie (một rating có thể có nhiều movie)
    @JsonIgnore
    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Movie> movies;
}