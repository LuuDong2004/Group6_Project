package group6.cinema_project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String image;
    private int duration;
    private java.sql.Timestamp releaseDate;
    private String rating;
    private String genre;
    private String language;
    private String trailer;
    // private String description; // Loại bỏ hoặc comment nếu không có trong DB
    @ElementCollection
    private List<String> directorNames;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(name = "Director_Movie",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors;

    public Set<Director> getDirectors() {
        return directors;
    }
    public void setDirectors(Set<Director> directors) {
        this.directors = directors;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public String getImage() {
        return image;
    }

    public String getTrailer() {
        return trailer;
    }

    public String getRating() {
        return rating;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate.toLocalDateTime().toLocalDate();
    }

    public List<String> getDirectorNames() {
        return directorNames;
    }

    // Setters có thể thêm nếu cần

    // Phương thức getSomeField() không cần thiết và có thể xóa
    public String getSomeField() {
        return this.name;
    }
}