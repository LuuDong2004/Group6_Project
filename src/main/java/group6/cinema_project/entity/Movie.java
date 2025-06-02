package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.*; // Import chính của Lombok
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Movie")
@Getter // Tự động tạo getters cho tất cả các trường
@Setter // Tự động tạo setters cho tất cả các trường
@NoArgsConstructor // Tự động tạo constructor không tham số (cần thiết cho JPA)
@AllArgsConstructor // Tự động tạo constructor với tất cả các trường
@ToString(exclude = {"actors", "directors"}) // Tự động tạo toString(), loại trừ các collection để tránh vòng lặp vô hạn
@EqualsAndHashCode(exclude = {"actors", "directors"}) // Tự động tạo equals() và hashCode(), loại trừ các collection
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "duration")
    private Integer duration;

    @Temporal(TemporalType.DATE)
    @Column(name = "releaseDate")
    private Date releaseDate;

    @Column(name = "rating", length = 50)
    private String rating;

    @Column(name = "genre", length = 255)
    private String genre;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "trailer", length = 255)
    private String trailer;

    @ManyToMany
    @JoinTable(
        name = "Actor_Movie",
        joinColumns = @JoinColumn(name = "MovieId"),
        inverseJoinColumns = @JoinColumn(name = "ActorId")
    )
    private Set<Actor> actors;

    @ManyToMany
    @JoinTable(
        name = "Director_Movie",
        joinColumns = @JoinColumn(name = "MovieId"),
        inverseJoinColumns = @JoinColumn(name = "DirectorId")
    )
    private Set<Director> directors;

    // @OneToMany(mappedBy = "movie")
    // private Set<ScreeningSchedule> screeningSchedules;
}