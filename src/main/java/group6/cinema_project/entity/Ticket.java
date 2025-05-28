package group6.cinema_project.entity;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@Table(name = "Ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String qrCode;
    private String description;
    private double price;

//    @ManyToOne
    @Column(name = "SeatId")
    private int seatId;

//    @ManyToOne
    @Column(name = "ScreeningScheduleId")
    private int screeningScheduleId;

    @ManyToOne
    @JoinColumn(name = "InvoiceId", nullable = false)
    private Invoice invoice;


    public Ticket() {

    }

    public Ticket(int id, String qrCode, String description, double price, int seatId, int screeningScheduleId, Invoice invoice) {
        this.id = id;
        this.qrCode = qrCode;
        this.description = description;
        this.price = price;
        this.seatId = seatId;
        this.screeningScheduleId = screeningScheduleId;
        this.invoice = invoice;
    }
}
