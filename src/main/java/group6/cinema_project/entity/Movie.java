package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String image;
    private int duration; // thời lượng tính bằng phút
    @Column(name = "release_date")
    private Date releaseDate;
    private double rating;
    private String genre; // thể loại phim
    private String language;
    private String trailer;
    

}
