package group6.cinema_project.entity;

<<<<<<< Updated upstream

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

=======
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;

@Data
>>>>>>> Stashed changes
@Entity
public class Booking {
    @Id
<<<<<<< Updated upstream
    private Long id;
    private String bookingDate;
    private Double totalAmount;
    private String expiryDate;
    private String bookingCode;
    private String bookingStatus;
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_schedule_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningSchedule schedule;

    @Column(name = "booking_code")
    private String code;
    @Column(name = "total_amount")
    private double amount;
    @Column(name = "booking_status")
    private String status;
    @Column(name = "booking_date")
    private LocalDate date;
    @Column(name = "expiry_date")
    private Date expiryDate; // ngày hiệu lực của booking
    @Column(name = "notes")
>>>>>>> Stashed changes
    private String notes;

<<<<<<< Updated upstream
    @ManyToOne
    private ScreeningSchedule screeningSchedule;

    @ManyToOne
    private User user; // Thay Users thành User

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public ScreeningSchedule getScreeningSchedule() { return screeningSchedule; }
    public void setScreeningSchedule(ScreeningSchedule screeningSchedule) { this.screeningSchedule = screeningSchedule; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
=======
}
>>>>>>> Stashed changes
