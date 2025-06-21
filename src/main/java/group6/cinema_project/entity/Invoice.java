package group6.cinema_project.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreatedDate
    @Column(name = "paymentDateTime")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "CustomerId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "EmployeeId")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "BookingId")
    private Booking booking;

}
