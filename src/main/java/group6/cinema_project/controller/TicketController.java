package group6.cinema_project.controller;

import group6.cinema_project.dto.TicketDto;
import group6.cinema_project.entity.Ticket;
import group6.cinema_project.repository.TicketRepository;
import group6.cinema_project.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("get/{id}")
    public ResponseEntity<List<TicketDto>> getTicketById(@PathVariable(name = "id") Integer CustomerId) {
        List<TicketDto> tickets = ticketService.getTicketsByCustomerId(CustomerId);

        return ResponseEntity.ok(tickets);
    }


}
