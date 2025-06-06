package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_review_user_movie_booking", 
                           columnNames = {"user_id", "movie_id", "booking_id"})
       },
       indexes = {
           @Index(name = "ix_reviews_movie_id_approved", columnList = "movie_id, is_approved")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(name = "rating")
    private Integer rating;
    
    @Column(name = "comment", columnDefinition = "NVARCHAR(MAX)")
    private String comment;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @PrePersist
    protected void onCreate() {
        reviewedAt = LocalDateTime.now();
    }
} 