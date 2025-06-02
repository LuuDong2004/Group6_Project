package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "Actor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"movies"})
@EqualsAndHashCode(exclude = {"movies"})
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name; 

    @Column(name = "image", length = 255)
    private String image; 

    @Lob
    @Column(name = "description")
    private String description; 

    @ManyToMany(mappedBy = "actors")
    private Set<Movie> movies;
}
