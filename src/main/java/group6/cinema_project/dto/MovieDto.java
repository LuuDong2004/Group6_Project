package group6.cinema_project.dto;

import group6.cinema_project.entity.Movie;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class MovieDto {
    private int id;
    private String name;
    private String image;
    private int duration; // thời lượng tính bằng phút


    private Date releaseDate;
    private double rating;
    private String genre; // thể loại phim
    private String language;
    private String trailer;
}