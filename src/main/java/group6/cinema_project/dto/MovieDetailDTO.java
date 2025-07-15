package group6.cinema_project.dto;

import java.util.List;

public class MovieDetailDto {
    public Long id;
    public String name;
    public String image;
    public int duration;
    public String release_date;
    public double rating;
    public String genre;
    public String language;
    public String format;
    public String trailer;
    public String summary;
    public List<String> directors;
    public List<String> actors;
    public List<ReviewDto> reviews;
    public List<PersonDto> actorsData;
    public List<PersonDto> directorsData;
} 