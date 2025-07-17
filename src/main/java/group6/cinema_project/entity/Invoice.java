package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Thay đổi từ int thành Integer để nhất quán với các entity khác

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
