package group6.cinema_project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "voucher")
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(name = "discount_amount")
    private double discountAmount;
    @Column(name = "discount_percent")
    private double discountPercent;
    @Column(name = "expiry_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    // Constructor không tham số
    public DiscountCode() {}
    // Constructor đầy đủ
    public DiscountCode(Long id, String code, double discountAmount, double discountPercent, LocalDate expiryDate) {
        this.id = id;
        this.code = code;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.expiryDate = expiryDate;
    }
    // Getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
} 