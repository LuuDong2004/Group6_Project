package group6.cinema_project.entity.Qa;

import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_history") // Sử dụng snake_case cho table name
public class BookingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "show_time", nullable = false)
    private LocalDateTime showTime;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "ticket_price", nullable = false)
    private BigDecimal ticketPrice;

    // Constructor mặc định
    public BookingHistory() {
        this.bookingDate = LocalDateTime.now();
    }

    // Constructor với tham số
    public BookingHistory(User user, Movie movie, LocalDateTime showTime,
                          String seatNumber, BigDecimal ticketPrice) {
        this.user = user;
        this.movie = movie;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.bookingDate = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}