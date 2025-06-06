package group6.cinema_project.dto;

import lombok.Data;

@Data
public class TicketDto {
    private int id;
    private String qrCode;
    private String description;
    private double price;
    private int seatId;
    private int screeningScheduleId;
    private InvoiceDto invoice; // nhớ sửa lại sang InvoiceDto , muộn rồi đi ngủ thôi =))

}
