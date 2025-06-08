package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String qrCode;
    private String description;
    private double price;

    @ManyToOne
    @JoinColumn(name = "SeatId")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "ScreeningScheduleId")
    private Schedule screeningSchedule;

    @ManyToOne
    @JoinColumn(name = "InvoiceId", nullable = false)
    private Invoice invoice;



}
