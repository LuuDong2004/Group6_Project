package group6.cinema_project.service;

import group6.cinema_project.dto.TicketDto;
import group6.cinema_project.entity.Ticket;

import java.util.List;

public interface ITicketService {
    List<TicketDto> getTicketsByCustomerId(Integer userId);
}
