package group6.cinema_project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionSepayDto {
    private Integer id;
    private String transactionId;
    private Integer bookingId;
    private double amount;
    private String status;
    private String merchantCode;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
