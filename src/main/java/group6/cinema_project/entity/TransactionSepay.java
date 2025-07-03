package group6.cinema_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Table(name = "TransactionSepay")
@NoArgsConstructor
@Entity
@Data
public class TransactionSepay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String transactionId;


    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Số tiền giao dịch
    private double amount;

    // Trạng thái giao dịch: PENDING, COMPLETED, FAILED, CANCELLED, ...
    private String status;

    // Mã merchant (nếu dùng nhiều merchant)
    private String merchantCode;


    private LocalDate createdAt;

    // Thời gian cập nhật trạng thái cuối cùng
    private LocalDate updatedAt;

    // Thông tin mô tả/ghi chú (nếu cần)
    private String description;


}
