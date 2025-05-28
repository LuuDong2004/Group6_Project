package group6.cinema_project.dto;

import group6.cinema_project.entity.Movie;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Builder
public class MovieDto {

    private String name;
    private String image;
    private int duration; // thời lượng tính bằng phút


    private Date releaseDate;
    private double rating;
    private String genre; // thể loại phim
    private String language;
    private String trailer;

    public MovieDto() {

    }

    public MovieDto(String name, String image, int duration, Date releaseDate, double rating, String genre, String language, String trailer) {

        this.name = name;
        this.image = image;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.genre = genre;
        this.language = language;
        this.trailer = trailer;
    }

    public Movie convertToModel() {
        return Movie.builder()
                .name(this.name)
                .image(this.image)
                .duration(this.duration)
                .releaseDate(this.releaseDate)
                .rating(this.rating)
                .genre(this.genre)
                .language(this.language)
                .trailer(this.trailer)
                .build();
    }
}