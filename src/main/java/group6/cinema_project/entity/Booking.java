package group6.cinema_project.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_schedule_id")
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
    private String notes;








}
