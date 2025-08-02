package group6.cinema_project.entity.Qa;

import group6.cinema_project.entity.Movie;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "rating", nullable = false)
    private int rating;
    
    @Column(name = "comment", length = 1000)
    private String comment;
    
    @Column(name = "date")
    private LocalDateTime date;

    // Constructor mặc định
    public Review() {
        this.date = LocalDateTime.now();
    }

    // Constructor với tham số
    public Review(Movie movie, Long userId, int rating, String comment) {
        this.movie = movie;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}