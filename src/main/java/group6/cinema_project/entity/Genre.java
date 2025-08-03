package group6.cinema_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "movies" })
@EqualsAndHashCode(exclude = { "movies" })
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name; // Tên thể loại

    @Column(name = "description", length = 255)
    private String description; // Mô tả thể loại

    // Quan hệ Many-to-Many với Movie (một genre có thể thuộc nhiều movie, một movie
    // có thể có nhiều genre)
    @JsonIgnore
    @ManyToMany(mappedBy = "genres", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Movie> movies;
}