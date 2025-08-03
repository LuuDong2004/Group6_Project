package group6.cinema_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // sua
    private String name;
    private String image;

    private Integer duration; // thời lượng tính bằng phút

    @Column(name = "release_date")
    private Date releaseDate;

    // Quan hệ Many-to-One với Rating (nhiều movie có thể có cùng một rating)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    // Quan hệ Many-to-Many với Genre (một movie có thể có nhiều genre, một genre có
    // thể thuộc nhiều movie)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;
    private String language;

    private String trailer;

    private String description;

    private String status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "Actor_Movie", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> actors;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "Director_Movie", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "director_id"))
    private Set<Director> directors;

}
