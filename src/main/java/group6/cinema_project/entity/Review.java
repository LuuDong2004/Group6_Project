package group6.cinema_project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
    
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
    public Review(Long movieId, Long userId, int rating, String comment) {
        this.movieId = movieId;
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

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
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