package group6.cinema_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private String rating; // double => string
    private String genre; // thể loại phim
    private String language;

    private String trailer;

    private String description;

    // Soft delete fields - Các trường để hỗ trợ soft delete
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // Mặc định là false (chưa bị xóa)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Thời gian thực hiện soft delete

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
