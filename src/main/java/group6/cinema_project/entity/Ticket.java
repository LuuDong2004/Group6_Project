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
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "screening_schedule_id")
    private ScreeningSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;



}
