package group6.cinema_project.dto;

import group6.cinema_project.entity.Invoice;
import group6.cinema_project.entity.Ticket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Builder
public class TicketDto {
    private int id;
    private String qrCode;
    private String description;
    private double price;
    private int seatId;
    private int screeningScheduleId;
    private InvoiceDto invoice; // nhớ sửa lại sang InvoiceDto , muộn rồi đi ngủ thôi =))

    public TicketDto() {
    }

    public TicketDto(int id, String qrCode, String description, double price, int seatId, int screeningScheduleId, InvoiceDto invoice) {
        this.id = id;
        this.qrCode = qrCode;
        this.description = description;
        this.price = price;
        this.seatId = seatId;
        this.screeningScheduleId = screeningScheduleId;
        this.invoice = invoice;
    }
}
