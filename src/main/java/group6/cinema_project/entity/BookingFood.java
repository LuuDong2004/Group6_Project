package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "BookingFood")
public class BookingFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price; // Giá tại thời điểm đặt

    @Column(name = "status")
    private String status; // PENDING, PAID, CANCELLED
} 