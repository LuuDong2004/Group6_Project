package group6.cinema_project.service;

import group6.cinema_project.dto.TicketDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Ticket;
import group6.cinema_project.repository.TicketRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService implements ITicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ModelMapper modelMapper;
    public TicketService() {
    }

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    public List<TicketDto> getTicketsByCustomerId(Integer customerId) {
        System.out.println("Searching tickets for Customer ID: " + customerId);

        List<Ticket> tickets = ticketRepository.findTicketsByCustomerId(customerId);

        System.out.println("Tickets found: " + tickets.size());
        tickets.forEach(ticket -> System.out.println("Found ticket ID: " + ticket.getId()));

        return tickets.stream()
                .map(ticket -> modelMapper.map(ticket, TicketDto.class))
                .collect(Collectors.toList());
    }
}
