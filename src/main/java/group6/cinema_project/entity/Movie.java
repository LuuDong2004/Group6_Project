package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "image")
    private String image;
    private int duration; // thời lượng tính bằng phút
    @Column(name = "release_date")
    private Date releaseDate;
    private double rating;
    private String genre; // thể loại phim
    private String language;
    private String trailer;
    
    public Movie() {

    }

    public Movie(int id, String name, String image, int duration, Date releaseDate, double rating, String genre, String language, String trailer) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.genre = genre;
        this.language = language;
        this.trailer = trailer;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", duration=" + duration +
                ", releaseDate=" + releaseDate +
                ", rating=" + rating +
                ", genre='" + genre + '\'' +
                ", language='" + language + '\'' +
                ", trailer='" + trailer + '\'' +
                '}';
    }
}
