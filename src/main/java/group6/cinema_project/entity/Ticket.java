package group6.cinema_project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Ticket {
    @Id
    private Long id;
    private String qrCode;
    private String description;
    private Double price;
    private Long seatId;
    private Long invoiceId;

    @ManyToOne
<<<<<<< Updated upstream
    private ScreeningSchedule screeningSchedule;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public ScreeningSchedule getScreeningSchedule() { return screeningSchedule; }
    public void setScreeningSchedule(ScreeningSchedule screeningSchedule) { this.screeningSchedule = screeningSchedule; }
=======
    @JoinColumn(name = "seat_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "screening_schedule_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreeningSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice invoice;
>>>>>>> Stashed changes

}
