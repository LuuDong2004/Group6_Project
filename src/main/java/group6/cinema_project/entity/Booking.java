package group6.cinema_project.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Booking {
    @Id
    private Long id;
    private String bookingDate;
    private Double totalAmount;
    private String expiryDate;
    private String bookingCode;
    private String bookingStatus;
    private String notes;

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