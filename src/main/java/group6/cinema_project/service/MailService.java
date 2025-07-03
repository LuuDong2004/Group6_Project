package group6.cinema_project.service;

import group6.cinema_project.dto.BookingDto;

public interface MailService {
    void sendETicketEmail(BookingDto booking, String userEmail);

}
